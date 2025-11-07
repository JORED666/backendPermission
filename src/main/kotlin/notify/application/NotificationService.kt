package notify.application

import notify.domain.entities.Notify
import notify.infrastructure.websocket.WebSocketManager
import students.domain.IStudentRepository
import permitsTeacher.domain.IPermitTeacherRepository
import tutors.domain.ITutorRepository  // ← AGREGAR

class NotificationService(
    private val createNotification: CreateNotificationUseCase,
    private val webSocketManager: WebSocketManager,
    private val studentRepository: IStudentRepository,
    private val permitTeacherRepository: IPermitTeacherRepository,
    private val tutorRepository: ITutorRepository  // ← AGREGAR
) {
    
    // Notificar al tutor cuando un estudiante crea un permiso
    suspend fun notifyTutorNewPermit(studentId: Int, permitId: Int, studentName: String) {
        try {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("🔔 ENVIANDO NOTIFICACIÓN DE NUEVO PERMISO")
            println("  👨‍🎓 Estudiante ID: $studentId")
            println("  📋 Permiso ID: $permitId")
            
            // Obtener el tutor del estudiante
            val student = studentRepository.getById(studentId)
            
            if (student == null) {
                println("❌ ERROR: Estudiante $studentId no encontrado")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            
            val tutorId = student.tutorId
            
            if (tutorId == null) {
                println("❌ ERROR: Estudiante $studentId no tiene tutor asignado")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            
            println("  👨‍🏫 Tutor ID (tabla tutors): $tutorId")
            
            // 🔥 OBTENER EL USER ID DEL TUTOR
            val tutor = tutorRepository.getById(tutorId)
            
            if (tutor == null) {
                println("❌ ERROR: Tutor $tutorId no encontrado en la base de datos")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            
            val tutorUserId = tutor.userId
            println("  👤 User ID del tutor: $tutorUserId")
            
            val notification = Notify(
                senderId = studentId,
                receiverId = tutorUserId,  // ← USAR userId en lugar de tutorId
                type = "new_permit",
                message = "$studentName ha solicitado un nuevo permiso",
                relatedPermitId = permitId
            )
            
            println("  💾 Guardando notificación en BD...")
            val savedNotification = createNotification.execute(notification)
            println("  ✅ Notificación guardada con ID: ${savedNotification.notificationId}")
            
            println("  📡 Enviando por WebSocket al userId $tutorUserId...")
            webSocketManager.sendNotificationToUser(tutorUserId, savedNotification)
            println("  ✅ Notificación enviada por WebSocket")
            
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        } catch (e: Exception) {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("❌ ERROR notificando al tutor: ${e.message}")
            e.printStackTrace()
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        }
    }
    
    // Notificar al estudiante sobre el estado del permiso
    suspend fun notifyStudentPermitStatus(tutorId: Int, studentId: Int, permitId: Int, status: String) {
        try {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("📢 NOTIFICANDO CAMBIO DE ESTADO DE PERMISO")
            println("  👨‍🏫 Tutor ID: $tutorId")
            println("  👨‍🎓 Estudiante ID: $studentId")
            println("  📋 Permiso ID: $permitId")
            println("  📊 Estado: $status")
            
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
            
            println("  💾 Guardando notificación...")
            val savedNotification = createNotification.execute(notification)
            println("  ✅ Notificación guardada con ID: ${savedNotification.notificationId}")
            
            println("  📡 Enviando por WebSocket al estudiante $studentId...")
            webSocketManager.sendNotificationToUser(studentId, savedNotification)
            println("  ✅ Notificación enviada por WebSocket")
            
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        } catch (e: Exception) {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("❌ ERROR notificando al estudiante: ${e.message}")
            e.printStackTrace()
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        }
    }
    
    // Notificar a profesores cuando se aprueba un permiso
    suspend fun notifyTeachersPermitApproved(studentId: Int, permitId: Int, studentName: String) {
        try {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("👨‍🏫 NOTIFICANDO A PROFESORES SOBRE PERMISO APROBADO")
            println("  👨‍🎓 Estudiante ID: $studentId")
            println("  📋 Permiso ID: $permitId")
            
            // Obtener los profesores asignados al permiso
            val permitTeachers = permitTeacherRepository.getByPermitId(permitId)
            println("  📊 Total profesores asignados: ${permitTeachers.size}")
            
            for (pt in permitTeachers) {
                println("  📤 Notificando a profesor ID: ${pt.teacherId}")
                
                val notification = Notify(
                    senderId = studentId,
                    receiverId = pt.teacherId,
                    type = "permit_assigned",
                    message = "$studentName tiene un permiso aprobado asignado a ti",
                    relatedPermitId = permitId
                )
                
                val savedNotification = createNotification.execute(notification)
                webSocketManager.sendNotificationToUser(pt.teacherId, savedNotification)
                println("  ✅ Notificación enviada a profesor ${pt.teacherId}")
            }
            
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        } catch (e: Exception) {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("❌ ERROR notificando a profesores: ${e.message}")
            e.printStackTrace()
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        }
    }
}