package students.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import students.application.UpdateStudentUseCase
import students.domain.entities.Student
import students.domain.dto.*

class UpdateStudentController(
    private val updateStudent: UpdateStudentUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null || id <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val body = call.receive<UpdateStudentRequest>()

            if (body.userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID de usuario es inválido"))
                return
            }

            if (body.matricula.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("La matrícula es requerida"))
                return
            }

            val student = Student(
                studentId = id,
                userId = body.userId,
                enrollmentNumber = body.matricula,
                familyTutorPhone = body.telefonoTutorFamiliar,
                tutorId = body.tutorId
            )

            updateStudent.execute(student)

            call.respond(HttpStatusCode.OK, MessageResponse("Estudiante actualizado exitosamente"))
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