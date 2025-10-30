package teachers.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import teachers.application.DeleteTeacherUseCase
import teachers.domain.dto.*

class DeleteTeacherController(
    private val deleteTeacher: DeleteTeacherUseCase
) {
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }
            
            deleteTeacher.execute(id)
            
            call.respond(HttpStatusCode.OK, MessageResponse("Teacher eliminado exitosamente"))
        } catch (error: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(error.message ?: "Teacher no encontrado")
            )
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}