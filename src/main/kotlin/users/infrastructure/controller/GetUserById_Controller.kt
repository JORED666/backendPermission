package users.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import users.application.GetUserByIdUseCase
import users.domain.dto.*

class GetUserByIdController(private val getUserById: GetUserByIdUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val user = getUserById.execute(id)

            if (user == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Usuario no encontrado"))
                return
            }

            call.respond(HttpStatusCode.OK, SingleUserResponse(UserResponse.fromUser(user)))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}