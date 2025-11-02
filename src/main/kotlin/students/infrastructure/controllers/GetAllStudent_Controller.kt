package students.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import students.application.GetAllStudentsWithDetailsUseCase
import students.domain.dto.*

class GetAllStudentController(
    private val getAllStudents: GetAllStudentsWithDetailsUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val studentsData = getAllStudents.execute()
            
            val students = studentsData.map { StudentWithDetailsResponse.fromMap(it) }

            call.respond(HttpStatusCode.OK, StudentListResponse(
                students = students,
                total = students.size
            ))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}