package permition.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import permition.application.DeletePermitUseCase
import permition.domain.dto.*

class DeletePermitController(
    private val deletePermit: DeletePermitUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }
            
            deletePermit.execute(id)
            
            call.respond(HttpStatusCode.OK, MessageResponse("Permiso eliminado exitosamente"))
        } catch (error: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(error.message ?: "Permiso no encontrado")
            )
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}