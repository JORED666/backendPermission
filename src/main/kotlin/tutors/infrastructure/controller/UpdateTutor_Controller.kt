package tutors.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import tutors.application.UpdateTutorUseCase
import tutors.domain.entities.Tutor
import tutors.domain.dto.*

@Serializable
data class UpdateTutorRequest(
    val userId: Int
)

class UpdateTutorController(private val updateTutor: UpdateTutorUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val body = call.receive<UpdateTutorRequest>()

            if (body.userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID de usuario es inválido"))
                return
            }

            val tutor = Tutor(
                tutorId = id,
                userId = body.userId
            )

            updateTutor.execute(tutor)

            call.respond(HttpStatusCode.OK, MessageResponse("Tutor actualizado exitosamente"))
        } catch (error: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(error.message ?: "Error desconocido"))
        }
    }
}