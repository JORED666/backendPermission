package students.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import students.application.GetStudentsByTutorIdUseCase
import students.domain.dto.*

class GetStudentsByTutorIdController(
    private val getStudentsByTutorId: GetStudentsByTutorIdUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val tutorId = call.parameters["tutorId"]?.toIntOrNull()

            if (tutorId == null || tutorId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("tutorId inválido"))
                return
            }

            val studentsData = getStudentsByTutorId.execute(tutorId)
            
            val students = studentsData.map { StudentWithDetailsResponse.fromMap(it) }
            
            call.respond(HttpStatusCode.OK, StudentListResponse(
                students = students,
                total = students.size
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