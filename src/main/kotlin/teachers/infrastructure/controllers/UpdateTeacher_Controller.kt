package teachers.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import teachers.application.UpdateTeacherUseCase
import teachers.domain.entities.Teacher
import teachers.domain.dto.*

@Serializable
data class UpdateTeacherRequest(
    val userId: Int
)

class UpdateTeacherController(
    private val updateTeacher: UpdateTeacherUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }
            
            val body = call.receive<UpdateTeacherRequest>()

            if (body.userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID de usuario es inválido"))
                return
            }

            val teacher = Teacher(
                teacherId = id,
                userId = body.userId
            )

            updateTeacher.execute(teacher)

            call.respond(HttpStatusCode.OK, MessageResponse("Teacher actualizado exitosamente"))
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