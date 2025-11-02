package permitsTeacher.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import permitsTeacher.application.GetAllPermitTeacherUseCase
import permitsTeacher.domain.dto.*

class GetAllPermitTeacherController(
    private val getAllPermitTeacher: GetAllPermitTeacherUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val permitsTeachersData = getAllPermitTeacher.execute()
            
            val permitsTeachers = permitsTeachersData.map { 
                PermitTeacherWithDetailsResponse.fromMap(it) 
            }

            call.respond(HttpStatusCode.OK, PermitTeacherListResponse(
                permitsTeachers = permitsTeachers,
                total = permitsTeachers.size
            ))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}