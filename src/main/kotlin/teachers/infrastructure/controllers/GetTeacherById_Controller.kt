package teachers.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import teachers.application.GetTeacherByIdWithDetailsUseCase
import teachers.domain.dto.*

class GetTeacherByIdController(
    private val getTeacherByIdWithDetails: GetTeacherByIdWithDetailsUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }
            
            val teacher = getTeacherByIdWithDetails.execute(id)
            
            if (teacher == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Teacher no encontrado"))
                return
            }
            
            call.respond(HttpStatusCode.OK, SingleTeacherResponse(
                teacher = TeacherWithDetailsResponse.fromTeacherWithDetails(teacher)
            ))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}