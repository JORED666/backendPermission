package permition.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import permition.application.GetAllPermitsUseCase
import permition.domain.dto.*

class GetAllPermitsController(
    private val getAllPermits: GetAllPermitsUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val permits = getAllPermits.execute()
            
            call.respond(HttpStatusCode.OK, PermitListResponse(
                permits = permits.map { PermitResponse.fromPermit(it) },
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