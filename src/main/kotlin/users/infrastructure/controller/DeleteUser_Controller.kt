package users.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import users.application.DeleteUserUseCase
import users.domain.dto.MessageResponse
import users.domain.dto.ErrorResponse

class DeleteUserController(private val deleteUser: DeleteUserUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            deleteUser.execute(id)

            call.respond(HttpStatusCode.OK, MessageResponse("Usuario eliminado exitosamente"))
        } catch (error: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error.message ?: "Error desconocido"))
        }
    }
}