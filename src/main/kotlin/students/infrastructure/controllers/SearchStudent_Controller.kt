package students.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import students.application.SearchStudentUseCase
import students.domain.dto.*

class SearchStudentController(
    private val searchStudent: SearchStudentUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val query = call.request.queryParameters["q"]

            if (query.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El parámetro de búsqueda 'q' es requerido"))
                return
            }

            val studentsData = searchStudent.execute(query)
            
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