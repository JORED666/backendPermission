package tutors.infrastructure.controller

import core.cloudinary.CloudinaryService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import tutors.application.UpdateTutorUseCase
import tutors.application.GetTutorByIdUseCase
import tutors.domain.entities.Tutor
import tutors.domain.dto.*

class UpdateTutorController(
    private val updateTutor: UpdateTutorUseCase,
    private val getTutorById: GetTutorByIdUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val currentTutor = getTutorById.execute(id)
            if (currentTutor == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Tutor no encontrado"))
                return
            }

            val multipart = call.receiveMultipart()
            var userId: Int? = null
            var newFirmaUrl: String? = null
            
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "userId") {
                            userId = part.value.toIntOrNull()
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "firma") {
                            println("📝 Procesando archivo de firma...")
                            val fileBytes = part.streamProvider().readBytes()
                            val fileName = part.originalFileName ?: "firma_${id}.png"
                            
                            newFirmaUrl = CloudinaryService.uploadFile(
                                fileBytes = fileBytes,
                                folder = "tutors/firmas",
                                fileName = fileName
                            )
                            println("✅ Firma subida a Cloudinary: $newFirmaUrl")
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El userId es requerido"))
                return
            }

            val finalFirmaUrl = newFirmaUrl ?: currentTutor.firmaUrl
            
            println("🔍 Firma final a guardar: $finalFirmaUrl")

            val tutor = Tutor(
                tutorId = id,
                userId = userId!!,
                firmaUrl = finalFirmaUrl
            )

            updateTutor.execute(tutor)

            val response = if (newFirmaUrl != null) {
                mapOf(
                    "message" to "Tutor actualizado exitosamente",
                    "firma_url" to finalFirmaUrl
                )
            } else {
                mapOf("message" to "Tutor actualizado exitosamente")
            }

            call.respond(HttpStatusCode.OK, response)
        } catch (error: Exception) {
            println("❌ Error en UpdateTutorController: ${error.message}")
            error.printStackTrace()
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error.message ?: "Error desconocido"))
        }
    }
}
