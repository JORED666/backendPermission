package teachers.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import teachers.application.GetAllTeachersWithDetailsUseCase
import teachers.domain.dto.*

class GetAllTeacherController(
    private val getAllTeachersWithDetails: GetAllTeachersWithDetailsUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val teachers = getAllTeachersWithDetails.execute()
            
            call.respond(HttpStatusCode.OK, TeacherListResponse(
                teachers = teachers.map { TeacherWithDetailsResponse.fromTeacherWithDetails(it) },
                total = teachers.size
            ))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}