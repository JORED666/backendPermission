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

            val existingStudent = updateStudent.getExistingStudent(id)
            if (existingStudent == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Estudiante no encontrado"))
                return
            }

            if (body.userId != null && body.userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID de usuario es inválido"))
                return
            }

            if (body.matricula != null && body.matricula.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("La matrícula no puede estar vacía"))
                return
            }

            if (body.telefonoTutorFamiliar != null && body.telefonoTutorFamiliar.isNotBlank()) {
                if (body.telefonoTutorFamiliar.length != 10 || !body.telefonoTutorFamiliar.all { it.isDigit() }) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("El teléfono debe tener exactamente 10 dígitos"))
                    return
                }
            }

            val student = Student(
                studentId = id,
                userId = body.userId ?: existingStudent.userId,
                enrollmentNumber = body.matricula ?: existingStudent.enrollmentNumber,
                familyTutorPhone = body.telefonoTutorFamiliar ?: existingStudent.familyTutorPhone,
                tutorId = body.tutorId ?: existingStudent.tutorId
            )

            updateStudent.execute(student)

            call.respond(HttpStatusCode.OK, MessageResponse("Estudiante actualizado exitosamente"))
            
        } catch (error: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error.message ?: "Error de validación")
            )
        } catch (error: Exception) {
            println("❌ Error en UpdateStudentController: ${error.message}")
            error.printStackTrace()
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Error interno del servidor")
            )
        }
    }
}