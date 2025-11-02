package permitsTeacher.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import permitsTeacher.application.DeletePermitTeacherUseCase
import permitsTeacher.domain.dto.*

class DeletePermitTeacherController(
    private val deletePermitTeacher: DeletePermitTeacherUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val permitId = call.parameters["permitId"]?.toIntOrNull()
            val teacherId = call.parameters["teacherId"]?.toIntOrNull()

            if (permitId == null || permitId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID del permiso es inválido"))
                return
            }

            if (teacherId == null || teacherId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID del profesor es inválido"))
                return
            }

            deletePermitTeacher.execute(permitId, teacherId)

            call.respond(HttpStatusCode.OK, MessageResponse("Asignación de permiso eliminada exitosamente"))
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