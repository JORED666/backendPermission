package history.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import history.application.UpdateHistoryStatusUseCase
import history.domain.dto.*

class UpdateHistoryStatusController(
    private val updateHistoryStatus: UpdateHistoryStatusUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null || id <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val body = call.receive<UpdateHistoryStatusRequest>()

            if (body.estado.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El estado es requerido"))
                return
            }

            updateHistoryStatus.execute(id, body.estado)

            call.respond(HttpStatusCode.OK, MessageResponse("Estado del historial actualizado exitosamente"))
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