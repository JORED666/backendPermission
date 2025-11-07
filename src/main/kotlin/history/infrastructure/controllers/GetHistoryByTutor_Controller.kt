package history.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import history.application.GetHistoryByTutorUseCase
import history.domain.dto.HistoryListByTutorResponse

class GetHistoryByTutorController(
    private val getHistoryByTutorUseCase: GetHistoryByTutorUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val tutorId = call.parameters["tutorId"]?.toIntOrNull()
            
            if (tutorId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, 
                    mapOf("message" to "tutorId es requerido")
                )
                return
            }

            val histories = getHistoryByTutorUseCase.execute(tutorId)
            
            val response = HistoryListByTutorResponse(
                histories = histories,
                total = histories.size
            )
            
            call.respond(HttpStatusCode.OK, response)
            
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("message" to "Error al obtener historial: ${error.message}")
            )
        }
    }
}