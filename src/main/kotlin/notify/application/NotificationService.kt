package notify.application

import notify.domain.entities.Notify
import notify.infrastructure.websocket.WebSocketManager
import students.domain.IStudentRepository
import permitsTeacher.domain.IPermitTeacherRepository

class NotificationService(
    private val createNotification: CreateNotificationUseCase,
    private val webSocketManager: WebSocketManager,
    private val studentRepository: IStudentRepository,
    private val permitTeacherRepository: IPermitTeacherRepository
) {
    
    // Notificar al tutor cuando un estudiante crea un permiso
    suspend fun notifyTutorNewPermit(studentId: Int, permitId: Int, studentName: String) {
        try {
            // Obtener el tutor del estudiante
            val student = studentRepository.getById(studentId)
            val tutorId = student?.tutorId ?: return
            
            val notification = Notify(
                senderId = studentId,
                receiverId = tutorId,
                type = "new_permit",
                message = "$studentName ha solicitado un nuevo permiso",
                relatedPermitId = permitId
            )
            
            val savedNotification = createNotification.execute(notification)
            webSocketManager.sendNotificationToUser(tutorId, savedNotification)
        } catch (e: Exception) {
            println("Error notifying tutor: ${e.message}")
        }
    }
    
    // Notificar al estudiante sobre el estado del permiso
    suspend fun notifyStudentPermitStatus(tutorId: Int, studentId: Int, permitId: Int, status: String) {
        try {
            val statusText = when(status) {
                "approved" -> "aprobado"
                "rejected" -> "rechazado"
                else -> "actualizado"
            }
            
            val notification = Notify(
                senderId = tutorId,
                receiverId = studentId,
                type = "permit_status",
                message = "Tu permiso ha sido $statusText",
                relatedPermitId = permitId
            )
            
            val savedNotification = createNotification.execute(notification)
            webSocketManager.sendNotificationToUser(studentId, savedNotification)
        } catch (e: Exception) {
            println("Error notifying student: ${e.message}")
        }
    }
    
    // Notificar a profesores cuando se aprueba un permiso
    suspend fun notifyTeachersPermitApproved(studentId: Int, permitId: Int, studentName: String) {
        try {
            // Obtener los profesores asignados al permiso
            val permitTeachers = permitTeacherRepository.getByPermitId(permitId)
            
            for (pt in permitTeachers) {
                val notification = Notify(
                    senderId = studentId,
                    receiverId = pt.teacherId,
                    type = "permit_assigned",
                    message = "$studentName tiene un permiso aprobado asignado a ti",
                    relatedPermitId = permitId
                )
                
                val savedNotification = createNotification.execute(notification)
                webSocketManager.sendNotificationToUser(pt.teacherId, savedNotification)
            }
        } catch (e: Exception) {
            println("Error notifying teachers: ${e.message}")
        }
    }
}