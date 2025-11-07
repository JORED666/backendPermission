package users.domain.dto

import kotlinx.serialization.Serializable
import users.domain.entities.User

@Serializable
data class UserResponse(
    val id: Int?,
    val nombres: String,
    val apellido_paterno: String,
    val apellido_materno: String?,
    val email: String,
    val rol_id: Int,
    val fecha_registro: String 
) {
    companion object {
        fun fromUser(user: User): UserResponse {
            return UserResponse(
                id = user.userId,
                nombres = user.firstName,
                apellido_paterno = user.lastName,
                apellido_materno = user.secondLastName,
                email = user.email,
                rol_id = user.roleId,
                fecha_registro = user.registrationDate.toString()
            )
        }
    }
}

@Serializable
data class UserListResponse(
    val users: List<UserResponse>,
    val total: Int
)

@Serializable
data class SingleUserResponse(
    val user: UserResponse
)

@Serializable
data class CreateUserResponse(
    val message: String,
    val user: CreatedUserData
)

@Serializable
data class CreatedUserData(
    val userId: Int?,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val secondLastName: String?,
    val email: String,
    val phone: String?,
    val registrationDate: String,
    val roleId: Int
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
data class LoginResponse(
    val token: String,
    val userId: Int,
    val tutorId: Int? = null,
    val name: String,
    val email: String
)

@Serializable
data class LoginSuccessResponse(
    val message: String,
    val data: LoginResponse
)