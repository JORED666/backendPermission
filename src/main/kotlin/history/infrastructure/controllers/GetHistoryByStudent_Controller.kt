package history.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import history.application.GetHistoryByStudentUseCase
import history.domain.dto.*

class GetHistoryByStudentController(
    private val getHistoryByStudent: GetHistoryByStudentUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val studentId = call.parameters["studentId"]?.toIntOrNull()

            if (studentId == null || studentId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID del estudiante inválido"))
                return
            }

            val historiesData = getHistoryByStudent.execute(studentId)
            
            val histories = historiesData.map { HistoryWithDetailsResponse.fromMap(it) }

            call.respond(HttpStatusCode.OK, HistoryListResponse(
                histories = histories,
                total = histories.size
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