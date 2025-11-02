package history.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import history.application.GetHistoryByIdUseCase
import history.domain.dto.*

class GetHistoryByIdController(
    private val getHistoryById: GetHistoryByIdUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null || id <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val historyData = getHistoryById.execute(id)

            if (historyData == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Historial no encontrado"))
                return
            }

            val history = HistoryWithDetailsResponse.fromMap(historyData)

            call.respond(HttpStatusCode.OK, SingleHistoryResponse(history = history))
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