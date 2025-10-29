package users.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import users.application.GetAllUsersUseCase
import users.domain.dto.UserResponse
import users.domain.dto.UserListResponse
import users.domain.dto.ErrorResponse

class GetAllUsersController(private val getAllUsers: GetAllUsersUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val users = getAllUsers.execute()
            val response = UserListResponse(
                users = users.map { UserResponse.fromUser(it) },
                total = users.size
            )
            call.respond(HttpStatusCode.OK, response)
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError, 
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}