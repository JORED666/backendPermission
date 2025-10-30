package teachers.domain.dto

import kotlinx.serialization.Serializable
import teachers.domain.entities.Teacher
import teachers.domain.entities.TeacherWithDetails
import teachers.domain.entities.PersonalInfo
import teachers.domain.entities.RoleInfo

@Serializable
data class PersonalInfoResponse(
    val nombreCompleto: String,
    val email: String,
    val telefono: String?
)

@Serializable
data class RoleInfoResponse(
    val nombreRol: String,
    val descripcion: String?
)

@Serializable
data class TeacherWithDetailsResponse(
    val teacherId: Int,
    val userId: Int,
    val informacionPersonal: PersonalInfoResponse,
    val informacionRol: RoleInfoResponse,
    val fechaRegistro: String
) {
    companion object {
        fun fromTeacherWithDetails(teacher: TeacherWithDetails): TeacherWithDetailsResponse {
            return TeacherWithDetailsResponse(
                teacherId = teacher.teacherId,
                userId = teacher.userId,
                informacionPersonal = PersonalInfoResponse(
                    nombreCompleto = teacher.personalInfo.fullName,
                    email = teacher.personalInfo.email,
                    telefono = teacher.personalInfo.phone
                ),
                informacionRol = RoleInfoResponse(
                    nombreRol = teacher.roleInfo.roleName,
                    descripcion = teacher.roleInfo.description
                ),
                fechaRegistro = teacher.registrationDate.toString()
            )
        }
    }
}

@Serializable
data class TeacherResponse(
    val teacherId: Int?,
    val userId: Int
) {
    companion object {
        fun fromTeacher(teacher: Teacher): TeacherResponse {
            return TeacherResponse(
                teacherId = teacher.teacherId,
                userId = teacher.userId
            )
        }
    }
}

@Serializable
data class TeacherListResponse(
    val teachers: List<TeacherWithDetailsResponse>,
    val total: Int
)

@Serializable
data class SingleTeacherResponse(
    val teacher: TeacherWithDetailsResponse
)

@Serializable
data class CreateTeacherResponse(
    val message: String,
    val teacher: TeacherResponse
)

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)