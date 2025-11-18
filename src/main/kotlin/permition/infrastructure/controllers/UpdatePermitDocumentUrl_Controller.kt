package permition.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import permition.application.UpdatePermitDocumentUrlUseCase
import permition.domain.dto.ErrorResponse
import permition.domain.dto.MessageResponse

@Serializable
data class UpdatePermitDocumentUrlRequest(
    val permitDocumentUrl: String
)

class UpdatePermitDocumentUrlController(
    private val updatePermitDocumentUrl: UpdatePermitDocumentUrlUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }
            
            val request = call.receive<UpdatePermitDocumentUrlRequest>()
            
            updatePermitDocumentUrl.execute(id, request.permitDocumentUrl)
            
            call.respond(
                HttpStatusCode.OK, 
                MessageResponse("URL del documento del permiso actualizada exitosamente")
            )
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