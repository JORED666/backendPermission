package notify.application

import notify.domain.INotifyRepository
import notify.domain.dto.NotificationWithDetailsResponse
import notify.domain.entities.Notify
import notify.infrastructure.websocket.WebSocketManager
import permitsTeacher.domain.IPermitTeacherRepository
import students.domain.IStudentRepository
import teachers.domain.ITeacherRepository
import tutors.domain.ITutorRepository

class NotificationService(
        private val createNotification: CreateNotificationUseCase,
        private val webSocketManager: WebSocketManager,
        private val studentRepository: IStudentRepository,
        private val permitTeacherRepository: IPermitTeacherRepository,
        private val tutorRepository: ITutorRepository,
        private val teacherRepository: ITeacherRepository,
        private val notifyRepository: INotifyRepository // ← AGREGAR para obtener detalles completos
) {

    private suspend fun getNotificationWithDetails(
            notificationId: Int
    ): NotificationWithDetailsResponse? {
        return try {
            notifyRepository.getNotificationWithDetails(notificationId)
        } catch (e: Exception) {
            println("❌ Error obteniendo detalles de notificación $notificationId: ${e.message}")
            null
        }
    }

    suspend fun notifyTutorNewPermit(studentId: Int, permitId: Int, studentName: String) {
        try {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("🔔 ENVIANDO NOTIFICACIÓN DE NUEVO PERMISO")
            println("  👨‍🎓 Estudiante ID: $studentId")
            println("  📋 Permiso ID: $permitId")

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

            val tutor = tutorRepository.getById(tutorId)

            if (tutor == null) {
                println("❌ ERROR: Tutor $tutorId no encontrado en la base de datos")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }

            val tutorUserId = tutor.userId
            val studentUserId = student.userId

            println("  👤 User ID del tutor: $tutorUserId")
            println("  👤 User ID del estudiante: $studentUserId")

            val notification =
                    Notify(
                            senderId = studentUserId,
                            receiverId = tutorUserId,
                            type = "new_permit",
                            message = "$studentName ha solicitado un nuevo permiso",
                            relatedPermitId = permitId
                    )

            println("  💾 Guardando notificación en BD...")
            val savedNotification = createNotification.execute(notification)
            println("  ✅ Notificación guardada con ID: ${savedNotification.notificationId}")

            if (savedNotification.notificationId != null) {
                println("  📊 Obteniendo detalles completos de la notificación...")
                val notificationWithDetails =
                        getNotificationWithDetails(savedNotification.notificationId)

                if (notificationWithDetails != null) {
                    println("  📡 Enviando notificación CON DETALLES por WebSocket...")
                    webSocketManager.sendNotificationWithDetails(
                            tutorUserId,
                            notificationWithDetails
                    )
                    println("  ✅ Notificación con detalles enviada exitosamente")
                } else {
                    println("  ⚠️ No se pudieron obtener detalles, enviando notificación simple...")
                    webSocketManager.sendNotificationToUser(tutorUserId, savedNotification)
                }
            } else {
                println("  ⚠️ Notificación sin ID, enviando simple...")
                webSocketManager.sendNotificationToUser(tutorUserId, savedNotification)
            }

            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        } catch (e: Exception) {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("❌ ERROR notificando al tutor: ${e.message}")
            e.printStackTrace()
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        }
    }

    suspend fun notifyStudentPermitStatus(
            tutorId: Int,
            studentId: Int,
            permitId: Int,
            status: String
    ) {
        try {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("📢 NOTIFICANDO CAMBIO DE ESTADO DE PERMISO")
            println("  👨‍🏫 Tutor ID: $tutorId")
            println("  👨‍🎓 Estudiante ID: $studentId")
            println("  📋 Permiso ID: $permitId")
            println("  📊 Estado: $status")

            val tutor = tutorRepository.getById(tutorId)
            if (tutor == null) {
                println("❌ ERROR: Tutor $tutorId no encontrado")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            val tutorUserId = tutor.userId

            val student = studentRepository.getById(studentId)
            if (student == null) {
                println("❌ ERROR: Estudiante $studentId no encontrado")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            val studentUserId = student.userId

            println("  👤 User ID del tutor: $tutorUserId")
            println("  👤 User ID del estudiante: $studentUserId")

            val statusText =
                    when (status) {
                        "approved" -> "aprobado"
                        "rejected" -> "rechazado"
                        else -> "actualizado"
                    }

            val notification =
                    Notify(
                            senderId = tutorUserId,
                            receiverId = studentUserId,
                            type = "permit_status",
                            message = "Tu permiso ha sido $statusText",
                            relatedPermitId = permitId
                    )

            println("  💾 Guardando notificación...")
            val savedNotification = createNotification.execute(notification)
            println("  ✅ Notificación guardada con ID: ${savedNotification.notificationId}")

            if (savedNotification.notificationId != null) {
                val notificationWithDetails =
                        getNotificationWithDetails(savedNotification.notificationId)

                if (notificationWithDetails != null) {
                    println("  📡 Enviando notificación CON DETALLES por WebSocket...")
                    webSocketManager.sendNotificationWithDetails(
                            studentUserId,
                            notificationWithDetails
                    )
                    println("  ✅ Notificación con detalles enviada exitosamente")
                } else {
                    webSocketManager.sendNotificationToUser(studentUserId, savedNotification)
                }
            } else {
                webSocketManager.sendNotificationToUser(studentUserId, savedNotification)
            }

            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        } catch (e: Exception) {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("❌ ERROR notificando al estudiante: ${e.message}")
            e.printStackTrace()
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        }
    }

    suspend fun notifyTeachersPermitApproved(studentId: Int, permitId: Int, studentName: String) {
        try {
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("👨‍🏫 NOTIFICANDO A PROFESORES SOBRE PERMISO APROBADO")
            println("  👨‍🎓 Estudiante ID: $studentId")
            println("  📋 Permiso ID: $permitId")

            val student = studentRepository.getById(studentId)
            if (student == null) {
                println("❌ ERROR: Estudiante $studentId no encontrado")
                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                return
            }
            val studentUserId = student.userId
            println("  👤 User ID del estudiante: $studentUserId")

            val permitTeachers = permitTeacherRepository.getByPermitId(permitId)
            println("  📊 Total profesores asignados: ${permitTeachers.size}")

            for (pt in permitTeachers) {
                println("  📤 Notificando a profesor ID: ${pt.teacherId}")

                val teacher = teacherRepository.getById(pt.teacherId)
                if (teacher == null) {
                    println("  ⚠️ Profesor ${pt.teacherId} no encontrado, saltando...")
                    continue
                }
                val teacherUserId = teacher.userId
                println("    👤 User ID del profesor: $teacherUserId")

                val notification =
                        Notify(
                                senderId = studentUserId,
                                receiverId = teacherUserId,
                                type = "permit_assigned",
                                message = "$studentName tiene un permiso aprobado asignado a ti",
                                relatedPermitId = permitId
                        )

                val savedNotification = createNotification.execute(notification)

                if (savedNotification.notificationId != null) {
                    val notificationWithDetails =
                            getNotificationWithDetails(savedNotification.notificationId)

                    if (notificationWithDetails != null) {
                        webSocketManager.sendNotificationWithDetails(
                                teacherUserId,
                                notificationWithDetails
                        )
                        println(
                                "  ✅ Notificación con detalles enviada a profesor userId $teacherUserId"
                        )
                    } else {
                        webSocketManager.sendNotificationToUser(teacherUserId, savedNotification)
                        println("  ⚠️ Notificación simple enviada a profesor userId $teacherUserId")
                    }
                } else {
                    webSocketManager.sendNotificationToUser(teacherUserId, savedNotification)
                }
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
