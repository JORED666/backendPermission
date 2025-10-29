package tutors.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import tutors.application.DeleteTutorUseCase
import tutors.domain.dto.*

class DeleteTutorController(private val deleteTutor: DeleteTutorUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            deleteTutor.execute(id)

            call.respond(HttpStatusCode.OK, MessageResponse("Tutor eliminado exitosamente"))
        } catch (error: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error.message ?: "Error desconocido"))
        }
    }
}