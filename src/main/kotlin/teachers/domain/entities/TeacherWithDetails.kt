package teachers.domain.entities

import java.time.LocalDateTime

data class TeacherWithDetails(
    val teacherId: Int,
    val userId: Int,
    val personalInfo: PersonalInfo,
    val roleInfo: RoleInfo,
    val registrationDate: LocalDateTime
)

data class PersonalInfo(
    val fullName: String,
    val email: String,
    val phone: String?
)

data class RoleInfo(
    val roleName: String,
    val description: String?
)