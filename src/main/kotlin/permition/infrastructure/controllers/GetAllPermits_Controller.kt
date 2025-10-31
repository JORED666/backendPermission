package permition.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import permition.application.GetAllPermitsWithDetailsUseCase
import permition.domain.dto.*

class GetAllPermitsController(
    private val getAllPermitsWithDetails: GetAllPermitsWithDetailsUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val permits = getAllPermitsWithDetails.execute()
            
            call.respond(HttpStatusCode.OK, PermitListResponse(
                permits = permits.map { PermitWithDetailsResponse.fromPermitWithDetails(it) },
                total = permits.size
            ))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}