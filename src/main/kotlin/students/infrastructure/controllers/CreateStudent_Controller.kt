package students.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import students.application.CreateStudentUseCase
import students.domain.entities.Student
import students.domain.dto.*

class CreateStudentController(
    private val createStudent: CreateStudentUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val body = call.receive<CreateStudentRequest>()

            if (body.userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El ID de usuario es inválido"))
                return
            }

            if (body.matricula.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("La matrícula es requerida"))
                return
            }

            val student = Student(
                userId = body.userId,
                enrollmentNumber = body.matricula,
                familyTutorPhone = body.telefonoTutorFamiliar,
                tutorId = body.tutorId
            )

            val savedStudent = createStudent.execute(student)

            call.respond(HttpStatusCode.Created, CreateStudentResponse(
                message = "Estudiante creado exitosamente",
                student = StudentResponse.fromStudent(savedStudent)
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