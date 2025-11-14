package notify.application

import notify.domain.entities.Notify
import notify.infrastructure.websocket.WebSocketManager
import students.domain.IStudentRepository
import permitsTeacher.domain.IPermitTeacherRepository
import tutors.domain.ITutorRepository
import teachers.domain.ITeacherRepository  // ← AGREGAR para obtener userId de teachers

class NotificationService(
    private val createNotification: CreateNotificationUseCase,
    private val webSocketManager: WebSocketManager,
    private val studentRepository: IStudentRepository,
    private val permitTeacherRepository: IPermitTeacherRepository,
    private val tutorRepository: ITutorRepository,
    private val teacherRepository: ITeacherRepository  // ← AGREGAR
) {
    
    // Notificar al tutor cuando un estudiante crea un permiso
    suspend fun notifyTutorNewPermit(studentId: Int, permitId: Int, studentName: String) {
        try {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("🔔 ENVIANDO NOTIFICACIÓN DE NUEVO PERMISO")
            println("  👨‍🎓 Estudiante ID: $studentId")
            println("  📋 Permiso ID: $permitId")
            
            // Obtener el estudiante
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
            
            // ✅ Obtener el USER ID del tutor
            val tutor = tutorRepository.getById(tutorId)
            
            if (tutor == null) {
                println("❌ ERROR: Tutor $tutorId no encontrado en la base de datos")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            
            val tutorUserId = tutor.userId
            val studentUserId = student.userId  // ✅ También obtener userId del estudiante
            
            println("  👤 User ID del tutor: $tutorUserId")
            println("  👤 User ID del estudiante: $studentUserId")
            
            val notification = Notify(
                senderId = studentUserId,      // ✅ Usar userId del estudiante
                receiverId = tutorUserId,      // ✅ Usar userId del tutor
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
            
            // ✅ Obtener el userId del tutor
            val tutor = tutorRepository.getById(tutorId)
            if (tutor == null) {
                println("❌ ERROR: Tutor $tutorId no encontrado")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            val tutorUserId = tutor.userId
            
            // ✅ Obtener el userId del estudiante
            val student = studentRepository.getById(studentId)
            if (student == null) {
                println("❌ ERROR: Estudiante $studentId no encontrado")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            val studentUserId = student.userId
            
            println("  👤 User ID del tutor: $tutorUserId")
            println("  👤 User ID del estudiante: $studentUserId")
            
            val statusText = when(status) {
                "approved" -> "aprobado"
                "rejected" -> "rechazado"
                else -> "actualizado"
            }
            
            val notification = Notify(
                senderId = tutorUserId,        // ✅ Usar userId del tutor
                receiverId = studentUserId,    // ✅ Usar userId del estudiante
                type = "permit_status",
                message = "Tu permiso ha sido $statusText",
                relatedPermitId = permitId
            )
            
            println("  💾 Guardando notificación...")
            val savedNotification = createNotification.execute(notification)
            println("  ✅ Notificación guardada con ID: ${savedNotification.notificationId}")
            
            println("  📡 Enviando por WebSocket al userId $studentUserId...")
            webSocketManager.sendNotificationToUser(studentUserId, savedNotification)  // ✅ Usar userId
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
            
            // ✅ Obtener el userId del estudiante
            val student = studentRepository.getById(studentId)
            if (student == null) {
                println("❌ ERROR: Estudiante $studentId no encontrado")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            val studentUserId = student.userId
            println("  👤 User ID del estudiante: $studentUserId")
            
            // Obtener los profesores asignados al permiso
            val permitTeachers = permitTeacherRepository.getByPermitId(permitId)
            println("  📊 Total profesores asignados: ${permitTeachers.size}")
            
            for (pt in permitTeachers) {
                println("  📤 Notificando a profesor ID: ${pt.teacherId}")
                
                // ✅ Obtener el userId del profesor
                val teacher = teacherRepository.getById(pt.teacherId)
                if (teacher == null) {
                    println("  ⚠️ Profesor ${pt.teacherId} no encontrado, saltando...")
                    continue
                }
                val teacherUserId = teacher.userId
                println("    👤 User ID del profesor: $teacherUserId")
                
                val notification = Notify(
                    senderId = studentUserId,      // ✅ Usar userId del estudiante
                    receiverId = teacherUserId,    // ✅ Usar userId del profesor
                    type = "permit_assigned",
                    message = "$studentName tiene un permiso aprobado asignado a ti",
                    relatedPermitId = permitId
                )
                
                val savedNotification = createNotification.execute(notification)
                webSocketManager.sendNotificationToUser(teacherUserId, savedNotification)
                println("  ✅ Notificación enviada a profesor userId $teacherUserId")
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