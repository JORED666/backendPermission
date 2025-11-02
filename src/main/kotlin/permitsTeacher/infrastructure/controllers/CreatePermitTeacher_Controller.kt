package permitsTeacher.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import permitsTeacher.application.CreatePermitTeacherUseCase
import permitsTeacher.domain.entities.PermitTeacher
import permitsTeacher.domain.dto.*

class CreatePermitTeacherController(
    private val createPermitTeacher: CreatePermitTeacherUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val body = call.receive<CreatePermitTeacherRequest>()

            if (body.permitId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID del permiso es inválido"))
                return
            }

            if (body.teacherId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID del profesor es inválido"))
                return
            }

            val permitTeacher = PermitTeacher(
                permitId = body.permitId,
                teacherId = body.teacherId
            )

            val savedPermitTeacher = createPermitTeacher.execute(permitTeacher)

            call.respond(HttpStatusCode.Created, CreatePermitTeacherResponse(
                message = "Permiso asignado al profesor exitosamente",
                permitTeacher = PermitTeacherResponse.fromPermitTeacher(savedPermitTeacher)
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