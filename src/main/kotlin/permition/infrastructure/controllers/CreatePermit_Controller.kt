package permition.infrastructure.controllers

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import permition.application.CreatePermitUseCase
import permition.application.GetPermitByIdWithDetailsUseCase 
import permition.domain.entities.Permition
import permition.domain.entities.PermitReason
import permition.domain.entities.PermitStatus
import permition.domain.dto.*
import core.cloudinary.CloudinaryService
import java.time.LocalDate
import java.time.LocalDateTime

class CreatePermitController(
    private val createPermit: CreatePermitUseCase,
    private val getPermitByIdWithDetails: GetPermitByIdWithDetailsUseCase 
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val multipart = call.receiveMultipart()
            
            var studentId: Int? = null
            var tutorId: Int? = null
            var teacherIds: List<Int> = emptyList()
            var startDate: String? = null
            var endDate: String? = null
            var reason: String? = null
            var description: String? = null
            var cuatrimestre: Int? = null
            var evidenceUrl: String? = null
            var validationError: String? = null
            
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
                reason == null || description == null || cuatrimestre == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Faltan campos requeridos"))
                return
            }

            if (cuatrimestre!! !in 1..11) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El cuatrimestre debe estar entre 1 y 11"))
                return
            }

            val permit = Permition(
                studentId = studentId!!,
                tutorId = tutorId!!,
                teacherIds = teacherIds,
                startDate = LocalDate.parse(startDate),
                endDate = LocalDate.parse(endDate),
                reason = PermitReason.fromString(reason!!),
                description = description!!,
                cuatrimestre = cuatrimestre!!,
                evidence = evidenceUrl,
                status = PermitStatus.PENDING,
                requestDate = LocalDateTime.now()
            )

            val savedPermit = createPermit.execute(permit)

            val permitWithDetails = getPermitByIdWithDetails.execute(savedPermit.permitId!!)
            
            if (permitWithDetails == null) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error al obtener detalles del permiso"))
                return
            }

            call.respond(HttpStatusCode.Created, CreatePermitWithDetailsResponse(
                message = "Permiso creado exitosamente",
                permit = PermitWithDetailsResponse.fromPermitWithDetails(permitWithDetails)
            ))
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