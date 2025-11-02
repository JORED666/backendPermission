package permitsTeacher.domain.dto

import kotlinx.serialization.Serializable
import permitsTeacher.domain.entities.PermitTeacher

@Serializable
data class CreatePermitTeacherRequest(
    val permitId: Int,
    val teacherId: Int
)

@Serializable
data class PermitInfoResponse(
    val permitId: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val motivo: String,
    val estado: String,
    val cuatrimestre: Int,
    val nombreEstudiante: String,
    val matricula: String?
)

@Serializable
data class TeacherInfoResponse(
    val teacherId: Int,
    val nombreCompleto: String,
    val email: String
)

@Serializable
data class PermitTeacherWithDetailsResponse(
    val permitId: Int,
    val teacherId: Int,
    val informacionPermiso: PermitInfoResponse,
    val informacionProfesor: TeacherInfoResponse
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): PermitTeacherWithDetailsResponse {
            return PermitTeacherWithDetailsResponse(
                permitId = data["permit_id"] as Int,
                teacherId = data["teacher_id"] as Int,
                informacionPermiso = PermitInfoResponse(
                    permitId = data["permit_id"] as Int,
                    fechaInicio = data["start_date"].toString(),
                    fechaFin = data["end_date"].toString(),
                    motivo = data["reason"] as String,
                    estado = data["status"] as String,
                    cuatrimestre = data["cuatrimestre"] as Int,
                    nombreEstudiante = (data["student_name"] as String).trim(),
                    matricula = data["enrollment_number"] as? String
                ),
                informacionProfesor = TeacherInfoResponse(
                    teacherId = data["teacher_id"] as Int,
                    nombreCompleto = (data["teacher_name"] as String).trim(),
                    email = data["teacher_email"] as String
                )
            )
        }
    }
}

@Serializable
data class PermitTeacherResponse(
    val permitId: Int,
    val teacherId: Int
) {
    companion object {
        fun fromPermitTeacher(permitTeacher: PermitTeacher): PermitTeacherResponse {
            return PermitTeacherResponse(
                permitId = permitTeacher.permitId,
                teacherId = permitTeacher.teacherId
            )
        }
    }
}

@Serializable
data class PermitTeacherListResponse(
    val permitsTeachers: List<PermitTeacherWithDetailsResponse>,
    val total: Int
)

@Serializable
data class SinglePermitTeacherResponse(
    val permitTeacher: PermitTeacherWithDetailsResponse
)

@Serializable
data class CreatePermitTeacherResponse(
    val message: String,
    val permitTeacher: PermitTeacherResponse
)

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)