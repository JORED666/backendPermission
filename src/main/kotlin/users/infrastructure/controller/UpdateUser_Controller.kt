package users.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import users.application.UpdateUserUseCase
import users.domain.entities.User
import users.domain.dto.MessageResponse
import users.domain.dto.ErrorResponse

@Serializable
data class UpdateUserRequest(
    val firstName: String,
    val middleName: String? = null,
    val lastName: String,
    val secondLastName: String? = null,
    val email: String,
    val phone: String? = null,
    val roleId: Int
)

class UpdateUserController(private val updateUser: UpdateUserUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val body = call.receive<UpdateUserRequest>()

            if (body.firstName.isEmpty() || body.lastName.isEmpty() || body.email.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Faltan campos requeridos"))
                return
            }

            val user = User(
                userId = id,
                firstName = body.firstName,
                middleName = body.middleName,
                lastName = body.lastName,
                secondLastName = body.secondLastName,
                email = body.email,
                phone = body.phone,
                password = "",
                roleId = body.roleId
            )

            updateUser.execute(user)

            call.respond(HttpStatusCode.OK, MessageResponse("Usuario actualizado exitosamente"))
        } catch (error: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error.message ?: "Error desconocido"))
        }
    }
}