package students.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import students.application.GetStudentByUserIdUseCase
import students.domain.dto.*

class GetStudentByUserIdController(
    private val getStudentByUserId: GetStudentByUserIdUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.toIntOrNull()

            if (userId == null || userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID de usuario inválido"))
                return
            }

            val studentData = getStudentByUserId.execute(userId)

            if (studentData == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Estudiante no encontrado para el usuario"))
                return
            }

            val student = StudentWithDetailsResponse.fromMap(studentData)

            call.respond(HttpStatusCode.OK, SingleStudentResponse(student = student))
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