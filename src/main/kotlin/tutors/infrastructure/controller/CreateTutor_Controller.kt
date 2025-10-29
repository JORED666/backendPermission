package tutors.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import tutors.application.CreateTutorUseCase
import tutors.domain.entities.Tutor
import tutors.domain.dto.*

@Serializable
data class CreateTutorRequest(
    val userId: Int
)

class CreateTutorController(private val createTutor: CreateTutorUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val body = call.receive<CreateTutorRequest>()

            if (body.userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID de usuario es inválido"))
                return
            }

            val tutor = Tutor(userId = body.userId)
            val savedTutor = createTutor.execute(tutor)

            call.respond(HttpStatusCode.Created, CreateTutorResponse(
                message = "Tutor creado exitosamente",
                tutor = CreatedTutorData(
                    tutorId = savedTutor.tutorId,
                    userId = savedTutor.userId
                )
            ))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}