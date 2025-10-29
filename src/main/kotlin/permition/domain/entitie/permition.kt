package users.domain.entities

import java.time.LocalDateTime

data class permition(
    val permitionId: Int? = null,
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