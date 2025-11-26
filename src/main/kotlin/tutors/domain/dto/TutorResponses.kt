package tutors.domain.dto

import kotlinx.serialization.Serializable
import tutors.domain.entities.Tutor

@Serializable
data class TutorResponse(
    val tutor_id: Int?,
    val user_id: Int
) {
    companion object {
        fun fromTutor(tutor: Tutor): TutorResponse {
            return TutorResponse(
                tutor_id = tutor.tutorId,
                user_id = tutor.userId
            )
        }
    }
}

@Serializable
data class TutorListResponse(
    val tutors: List<TutorResponse>,
    val total: Int
)

@Serializable
data class SingleTutorResponse(
    val tutor: TutorResponse
)

@Serializable
data class CreateTutorResponse(
    val message: String,
    val tutor: CreatedTutorData
)

@Serializable
data class CreatedTutorData(
    val tutorId: Int?,
    val userId: Int
)

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class InformacionPersonal(
    val nombre_completo: String,
    val email: String,
    val telefono: String?
)

@Serializable
data class InformacionRol(
    val nombre_rol: String,
    val descripcion: String?
)

@Serializable
data class TutorDetailResponse(
    val tutor_id: Int,
    val user_id: Int,
    val informacion_personal: InformacionPersonal,
    val informacion_rol: InformacionRol,
    val fecha_registro: String,
    val firma_url: String? = null
)

@Serializable
data class TutorDetailListResponse(
    val tutors: List<TutorDetailResponse>,
    val total: Int
)
