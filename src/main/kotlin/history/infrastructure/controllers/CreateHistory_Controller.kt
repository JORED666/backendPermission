package history.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import history.application.CreateHistoryUseCase
import history.domain.entities.History
import history.domain.dto.*
import java.time.LocalDate

class CreateHistoryController(
    private val createHistory: CreateHistoryUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val body = call.receive<CreateHistoryRequest>()

            if (body.permitId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID del permiso es inválido"))
                return
            }

            if (body.studentId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID del estudiante es inválido"))
                return
            }

            if (body.motivo.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El motivo es requerido"))
                return
            }

            val startDate = body.fechaInicio?.let { LocalDate.parse(it) }
            val endDate = body.fechaFin?.let { LocalDate.parse(it) }

            val history = History(
                permitId = body.permitId,
                studentId = body.studentId,
                startDate = startDate,
                endDate = endDate,
                reason = body.motivo,
                status = body.estado
            )

            val savedHistory = createHistory.execute(history)

            call.respond(HttpStatusCode.Created, CreateHistoryResponse(
                message = "Historial de permiso creado exitosamente",
                history = HistoryResponse.fromHistory(savedHistory)
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