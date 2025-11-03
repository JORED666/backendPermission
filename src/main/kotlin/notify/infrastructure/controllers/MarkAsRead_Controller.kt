package notify.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import notify.application.MarkAsReadUseCase
import notify.domain.dto.*

class MarkAsReadController(
    private val markAsRead: MarkAsReadUseCase
) {
    
    suspend fun executeOne(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null || id <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            markAsRead.execute(id)

            call.respond(HttpStatusCode.OK, MessageResponse("Notificación marcada como leída"))
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
    
    suspend fun executeAll(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.toIntOrNull()

            if (userId == null || userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID de usuario inválido"))
                return
            }

            markAsRead.markAllAsRead(userId)

            call.respond(HttpStatusCode.OK, MessageResponse("Todas las notificaciones marcadas como leídas"))
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