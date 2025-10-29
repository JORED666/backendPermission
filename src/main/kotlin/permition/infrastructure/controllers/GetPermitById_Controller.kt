package permition.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import permition.application.GetPermitByIdUseCase
import permition.domain.dto.*

class GetPermitByIdController(
    private val getPermitById: GetPermitByIdUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }
            
            val permit = getPermitById.execute(id)
            
            if (permit == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Permiso no encontrado"))
                return
            }
            
            call.respond(HttpStatusCode.OK, SinglePermitResponse(
                permit = PermitResponse.fromPermit(permit)
            ))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}