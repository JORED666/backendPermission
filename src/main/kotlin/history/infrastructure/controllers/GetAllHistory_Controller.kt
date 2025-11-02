package history.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import history.application.GetAllHistoryUseCase
import history.domain.dto.*

class GetAllHistoryController(
    private val getAllHistory: GetAllHistoryUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val historiesData = getAllHistory.execute()
            
            val histories = historiesData.map { HistoryWithDetailsResponse.fromMap(it) }

            call.respond(HttpStatusCode.OK, HistoryListResponse(
                histories = histories,
                total = histories.size
            ))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}