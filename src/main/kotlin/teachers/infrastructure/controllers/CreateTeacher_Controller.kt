package teachers.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import teachers.application.CreateTeacherUseCase
import teachers.domain.entities.Teacher
import teachers.domain.dto.*

@Serializable
data class CreateTeacherRequest(
    val userId: Int
)

class CreateTeacherController(
    private val createTeacher: CreateTeacherUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val body = call.receive<CreateTeacherRequest>()

            if (body.userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID de usuario es inválido"))
                return
            }

            val teacher = Teacher(
                userId = body.userId
            )

            val savedTeacher = createTeacher.execute(teacher)

            call.respond(HttpStatusCode.Created, CreateTeacherResponse(
                message = "Teacher creado exitosamente",
                teacher = TeacherResponse.fromTeacher(savedTeacher)
            ))
        } catch (error: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error.message ?: "Error de validación")
            )
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}