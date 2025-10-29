package users.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import users.application.AuthServiceUseCase
import users.domain.dto.ErrorResponse
import users.domain.dto.LoginSuccessResponse

class AuthController(private val authService: AuthServiceUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val body = call.receive<Map<String, String>>()
            val email = body["email"]
            val password = body["password"]

            if (email == null || password == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Email y password son requeridos"))
                return
            }

            val loginData = authService.login(email, password)

            call.respond(HttpStatusCode.OK, LoginSuccessResponse(
                message = "Login exitoso",
                data = loginData
            ))
        } catch (error: Exception) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(error.message ?: "Error desconocido"))
        }
    }
}