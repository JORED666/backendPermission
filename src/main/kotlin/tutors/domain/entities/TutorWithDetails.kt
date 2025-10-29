package tutors.domain.entities

import java.time.LocalDateTime

data class TutorWithDetails(
    val tutorId: Int,
    val userId: Int,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val secondLastName: String?,
    val email: String,
    val phone: String?,
    val roleName: String,
    val roleDescription: String,
    val registrationDate: LocalDateTime
)