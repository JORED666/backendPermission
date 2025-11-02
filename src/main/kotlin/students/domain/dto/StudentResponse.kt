package students.domain.dto

import kotlinx.serialization.Serializable
import students.domain.entities.Student

@Serializable
data class CreateStudentRequest(
    val userId: Int,
    val matricula: String,
    val telefonoTutorFamiliar: String? = null,
    val tutorId: Int? = null
)

@Serializable
data class UpdateStudentRequest(
    val userId: Int,
    val matricula: String,
    val telefonoTutorFamiliar: String? = null,
    val tutorId: Int? = null
)

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
data class StudentWithDetailsResponse(
    val studentId: Int,
    val matricula: String,
    val telefonoTutorFamiliar: String?,
    val userId: Int,
    val tutorId: Int?,
    val informacionPersonal: PersonalInfoResponse,
    val informacionRol: RoleInfoResponse,
    val fechaRegistro: String
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): StudentWithDetailsResponse {
            return StudentWithDetailsResponse(
                studentId = data["student_id"] as Int,
                matricula = data["enrollment_number"] as? String ?: "",
                telefonoTutorFamiliar = data["family_tutor_phone"] as? String,
                userId = data["user_id"] as Int,
                tutorId = data["tutor_id"] as? Int,
                informacionPersonal = PersonalInfoResponse(
                    nombreCompleto = ((data["full_name"] as? String) ?: "").trim(),
                    email = data["email"] as? String ?: "",
                    telefono = data["phone"] as? String
                ),
                informacionRol = RoleInfoResponse(
                    nombreRol = data["role_name"] as? String ?: "",
                    descripcion = data["role_description"] as? String
                ),
                fechaRegistro = data["registration_date"]?.toString() ?: ""
            )
        }
    }
}

@Serializable
data class StudentResponse(
    val studentId: Int?,
    val matricula: String,
    val telefonoTutorFamiliar: String?,
    val userId: Int,
    val tutorId: Int?
) {
    companion object {
        fun fromStudent(student: Student): StudentResponse {
            return StudentResponse(
                studentId = student.studentId,
                matricula = student.enrollmentNumber,
                telefonoTutorFamiliar = student.familyTutorPhone,
                userId = student.userId,
                tutorId = student.tutorId
            )
        }
    }
}

@Serializable
data class StudentListResponse(
    val students: List<StudentWithDetailsResponse>,
    val total: Int
)

@Serializable
data class SingleStudentResponse(
    val student: StudentWithDetailsResponse
)

@Serializable
data class CreateStudentResponse(
    val message: String,
    val student: StudentResponse
)

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)