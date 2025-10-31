package permition.infrastructure.controllers

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import permition.application.UpdatePermitUseCase
import permition.application.GetPermitByIdUseCase
import permition.domain.entities.Permition
import permition.domain.entities.PermitReason
import permition.domain.entities.PermitStatus
import permition.domain.dto.*
import core.cloudinary.CloudinaryService
import java.time.LocalDate

class UpdatePermitController(
    private val updatePermit: UpdatePermitUseCase,
    private val getPermitById: GetPermitByIdUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }
            
            val existingPermit = getPermitById.execute(id)
            if (existingPermit == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Permiso no encontrado"))
                return
            }
            
            val multipart = call.receiveMultipart()
            
            var studentId: Int? = null
            var tutorId: Int? = null
            var teacherIds: List<Int> = emptyList()
            var startDate: String? = null
            var endDate: String? = null
            var reason: String? = null
            var description: String? = null
            var cuatrimestre: Int? = null 
            var status: String? = null
            var evidenceUrl: String? = existingPermit.evidence
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
                            
                            oldEvidenceUrl = existingPermit.evidence
                            
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
            
            if (studentId == null || tutorId == null || startDate == null || endDate == null || 
                reason == null || description == null || cuatrimestre == null || status == null) { 
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Faltan campos requeridos"))
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
                requestDate = existingPermit.requestDate
            )

            updatePermit.execute(permit)
            
            if (oldEvidenceUrl != null && oldEvidenceUrl != evidenceUrl) {
                try {
                    CloudinaryService.deleteFile(oldEvidenceUrl!!)
                } catch (e: Exception) {
                    println("⚠️ No se pudo eliminar el archivo antiguo: ${e.message}")
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
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}