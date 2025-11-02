package students.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import students.application.GetStudentByIdWithDetailsUseCase
import students.domain.dto.*

class GetStudentByIdController(
    private val getStudentById: GetStudentByIdWithDetailsUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null || id <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val studentData = getStudentById.execute(id)

            if (studentData == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Estudiante no encontrado"))
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