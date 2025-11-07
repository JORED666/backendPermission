package permition.infrastructure.controllers

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import permition.application.UpdatePermitUseCase
import permition.application.GetPermitByIdWithDetailsUseCase
import permition.domain.entities.Permition
import permition.domain.entities.PermitReason
import permition.domain.entities.PermitStatus
import permition.domain.dto.*
import core.cloudinary.CloudinaryService
import notify.application.NotificationService
import java.time.LocalDate

class UpdatePermitController(
    private val updatePermit: UpdatePermitUseCase,
    private val getPermitByIdWithDetails: GetPermitByIdWithDetailsUseCase,
    private val notificationService: NotificationService
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }
            
            val permitWithDetails = getPermitByIdWithDetails.execute(id)
            if (permitWithDetails == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Permiso no encontrado"))
                return
            }
            
            val existingTeacherIds = permitWithDetails.teachers.map { it.teacherId }
            
            val multipart = call.receiveMultipart()
            
            var studentId: Int? = permitWithDetails.studentInfo.studentId
            var tutorId: Int? = permitWithDetails.tutorInfo.tutorId
            var teacherIds: List<Int> = existingTeacherIds
            var startDate: String? = permitWithDetails.startDate.toString()
            var endDate: String? = permitWithDetails.endDate.toString()
            var reason: String? = permitWithDetails.reason.name
            var description: String? = permitWithDetails.description
            var cuatrimestre: Int? = permitWithDetails.cuatrimestre
            var status: String? = permitWithDetails.status.name
            var evidenceUrl: String? = permitWithDetails.evidence
            var validationError: String? = null
            var oldEvidenceUrl: String? = null
            
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "studentId" -> studentId = part.value.toIntOrNull()
                            "tutorId" -> tutorId = part.value.toIntOrNull()
                            "teacherIds" -> {
                                teacherIds = part.value.split(",")
                                    .mapNotNull { it.trim().toIntOrNull() }
                            }
                            "startDate" -> startDate = part.value
                            "endDate" -> endDate = part.value
                            "reason" -> reason = part.value
                            "description" -> description = part.value
                            "cuatrimestre" -> cuatrimestre = part.value.toIntOrNull()
                            "status" -> status = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "evidence") {
                            val fileBytes = part.streamProvider().readBytes()
                            val fileName = part.originalFileName
                            
                            val contentType = part.contentType?.contentType
                            if (contentType != "application" || part.contentType?.contentSubtype != "pdf") {
                                validationError = "Solo se permiten archivos PDF"
                                part.dispose()
                                return@forEachPart
                            }
                            
                            oldEvidenceUrl = permitWithDetails.evidence
                            
                            evidenceUrl = CloudinaryService.uploadFile(
                                fileBytes = fileBytes,
                                folder = "permits/evidences",
                                fileName = fileName
                            )
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }
            
            if (validationError != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(validationError!!))
                return
            }
            
            if (cuatrimestre!! !in 1..11) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El cuatrimestre debe estar entre 1 y 11"))
                return
            }

            val permit = Permition(
                permitId = id,
                studentId = studentId!!,
                tutorId = tutorId!!,
                teacherIds = teacherIds,
                startDate = LocalDate.parse(startDate),
                endDate = LocalDate.parse(endDate),
                reason = PermitReason.fromString(reason!!),
                description = description!!,
                cuatrimestre = cuatrimestre!!, 
                evidence = evidenceUrl,
                status = PermitStatus.fromString(status!!),
                requestDate = permitWithDetails.requestDate
            )

            updatePermit.execute(permit)
            
            val oldStatus = permitWithDetails.status.name
            val newStatus = status!!
            if (oldStatus != newStatus) {
                try {
                    if (newStatus.equals("approved", ignoreCase = true)) {
                        notificationService.notifyStudentPermitStatus(
                            tutorId = permit.tutorId,
                            studentId = permit.studentId,
                            permitId = id,
                            status = "approved"
                        )
                        println("Notificacion de aprobacion enviada al estudiante")
                        
                        notificationService.notifyTeachersPermitApproved(
                            studentId = permit.studentId,
                            permitId = id,
                            studentName = permitWithDetails.studentInfo.fullName
                        )
                        println("Notificaciones enviadas a los profesores")
                    } else if (newStatus.equals("rejected", ignoreCase = true)) {
                        notificationService.notifyStudentPermitStatus(
                            tutorId = permit.tutorId,
                            studentId = permit.studentId,
                            permitId = id,
                            status = "rejected"
                        )
                        println("Notificacion de rechazo enviada al estudiante")
                    }
                } catch (e: Exception) {
                    println("Error enviando notificaciones: ${e.message}")
                }
            }
            
            if (oldEvidenceUrl != null && oldEvidenceUrl != evidenceUrl) {
                try {
                    CloudinaryService.deleteFile(oldEvidenceUrl!!)
                } catch (e: Exception) {
                    println("No se pudo eliminar el archivo antiguo: ${e.message}")
                }
            }

            call.respond(HttpStatusCode.OK, MessageResponse("Permiso actualizado exitosamente"))
        } catch (error: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error.message ?: "Error de validación")
            )
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido: ${error.message}")
            )
        }
    }
}