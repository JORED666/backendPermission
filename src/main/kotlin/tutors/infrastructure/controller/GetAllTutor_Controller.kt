package tutors.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import tutors.application.GetAllTutorsWithDetailsUseCase
import tutors.domain.dto.*

class GetAllTutorsController(private val getAllTutorsWithDetails: GetAllTutorsWithDetailsUseCase) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val tutors = getAllTutorsWithDetails.execute()
            val response = TutorDetailListResponse(
                tutors = tutors.map { tutor ->
                    TutorDetailResponse(
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
                },
                total = tutors.size
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