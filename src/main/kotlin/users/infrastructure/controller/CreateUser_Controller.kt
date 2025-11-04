package users.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import users.application.CreateUserUseCase
import users.application.AuthServiceUseCase
import users.domain.entities.User
import users.domain.dto.*
import users.domain.utils.EmailValidator
import users.domain.IUserRepository 
import java.time.LocalDateTime

@Serializable
data class CreateUserRequest(
    val firstName: String,
    val middleName: String? = null,
    val lastName: String,
    val secondLastName: String? = null,
    val email: String,
    val phone: String? = null,
    val password: String,
    val roleId: Int? = null 
)

class CreateUserController(
    private val createUser: CreateUserUseCase,
    private val authService: AuthServiceUseCase,
    private val userRepo: IUserRepository 
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val body = call.receive<CreateUserRequest>()

            if (body.firstName.isEmpty() || body.lastName.isEmpty() || 
                body.email.isEmpty() || body.password.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, 
                    ErrorResponse("Faltan campos requeridos"))
                return
            }

            val roleInfo = EmailValidator.validateAndGetRole(body.email)
            
            if (!roleInfo.isValid) {
                call.respond(HttpStatusCode.BadRequest, 
                    ErrorResponse(roleInfo.errorMessage ?: "Correo inválido"))
                return
            }

            val user = User(
                firstName = body.firstName,
                middleName = body.middleName,
                lastName = body.lastName,
                secondLastName = body.secondLastName,
                email = body.email,
                phone = body.phone,
                password = body.password,
                registrationDate = LocalDateTime.now(),
                roleId = roleInfo.roleId 
            )

            val savedUser = authService.register(user)
            
            if (roleInfo.isTeacher && savedUser.userId != null) {
                userRepo.insertTeacher(savedUser.userId)
                println("Usuario ${savedUser.email} registrado como Tutor y Teacher")
            }

            call.respond(HttpStatusCode.Created, CreateUserResponse(
                message = "Usuario creado exitosamente",
                user = CreatedUserData(
                    userId = savedUser.userId,
                    firstName = savedUser.firstName,
                    middleName = savedUser.middleName,
                    lastName = savedUser.lastName,
                    secondLastName = savedUser.secondLastName,
                    email = savedUser.email,
                    phone = savedUser.phone,
                    registrationDate = savedUser.registrationDate.toString(),
                    roleId = savedUser.roleId
                )
            ))
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}