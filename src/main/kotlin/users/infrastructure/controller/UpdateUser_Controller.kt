package users.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import users.application.UpdateUserUseCase
import users.domain.dto.MessageResponse
import users.domain.dto.ErrorResponse
import users.domain.dto.UpdateUserRequest

class UpdateUserController(private val updateUser: UpdateUserUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val body = call.receive<UpdateUserRequest>()
            
            if (body.firstName == null && 
                body.middleName == null && 
                body.lastName == null && 
                body.secondLastName == null && 
                body.phone == null) {
                call.respond(
                    HttpStatusCode.BadRequest, 
                    ErrorResponse("Debe proporcionar al menos un campo para actualizar")
                )
                return
            }

            if (body.phone != null && body.phone.isNotEmpty()) {
                if (!body.phone.matches(Regex("^\\d{10}$"))) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("El teléfono debe tener exactamente 10 dígitos")
                    )
                    return
                }
            }

            updateUser.execute(id, body)
            
            call.respond(HttpStatusCode.OK, MessageResponse("Usuario actualizado exitosamente"))
            
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Error en la solicitud"))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error interno del servidor"))
        }
    }
}
