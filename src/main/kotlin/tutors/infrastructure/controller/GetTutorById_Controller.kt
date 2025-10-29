package tutors.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import tutors.application.GetTutorByIdWithDetailsUseCase
import tutors.domain.dto.*

class GetTutorByIdController(private val getTutorByIdWithDetails: GetTutorByIdWithDetailsUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                return
            }

            val tutor = getTutorByIdWithDetails.execute(id)

            if (tutor == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Tutor no encontrado"))
                return
            }

            val response = TutorDetailResponse(
                tutor_id = tutor.tutorId,
                user_id = tutor.userId,
                informacion_personal = InformacionPersonal(
                    nombre_completo = "${tutor.firstName} ${tutor.middleName ?: ""} ${tutor.lastName} ${tutor.secondLastName ?: ""}".trim(),
                    email = tutor.email,
                    telefono = tutor.phone
                ),
                informacion_rol = InformacionRol(
                    nombre_rol = tutor.roleName,
                    descripcion = tutor.roleDescription
                ),
                fecha_registro = tutor.registrationDate.toString()
            )

            call.respond(HttpStatusCode.OK, response)
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}