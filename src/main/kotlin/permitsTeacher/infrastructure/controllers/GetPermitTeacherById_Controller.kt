package permitsTeacher.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import permitsTeacher.application.GetPermitTeacherUseCase
import permitsTeacher.domain.dto.*

class GetPermitTeacherByIdController(
    private val getPermitTeacher: GetPermitTeacherUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val type = call.parameters["type"]
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null || id <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val permitsTeachersData = when (type) {
                "permit" -> getPermitTeacher.executeByPermitId(id)
                "teacher" -> getPermitTeacher.executeByTeacherId(id)
                else -> {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Tipo inválido. Use 'permit' o 'teacher'"))
                    return
                }
            }

            val permitsTeachers = permitsTeachersData.map { 
                PermitTeacherWithDetailsResponse.fromMap(it) 
            }

            call.respond(HttpStatusCode.OK, PermitTeacherListResponse(
                permitsTeachers = permitsTeachers,
                total = permitsTeachers.size
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