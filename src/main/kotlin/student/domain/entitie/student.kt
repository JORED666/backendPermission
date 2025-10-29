package users.domain.entitie

import java.time.LocalDateTime

data class student(
    val studentId: Int? = null,
    val enrollmentNumber: String,
    val familyTutorPhone: String,
    val userId: Int? = null,
    val firstName: String,
    val middleName: String? = null,
    val lastName: String,
    val secondLastName: String? = null,
    val email: String,
    val phone: String? = null,
)

data class CreateStudentRequest(
    val enrollmentNumber: String,
    val familyTutorPhone: String?,
    val userId: Int
)

data class UpdateStudentRequest(
    val enrollmentNumber: String?,
    val familyTutorPhone: String?
)
