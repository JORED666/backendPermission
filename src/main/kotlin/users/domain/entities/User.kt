package users.domain.entities

import java.time.LocalDateTime

data class User(
    val userId: Int? = null,
    val firstName: String,
    val middleName: String? = null,
    val lastName: String,
    val secondLastName: String? = null,
    val email: String,
    val phone: String? = null,
    val password: String,
    val registrationDate: LocalDateTime = LocalDateTime.now(),
    val roleId: Int
)