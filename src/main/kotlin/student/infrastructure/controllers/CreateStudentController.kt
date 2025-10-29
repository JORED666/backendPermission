package users.infrastructure.controllers


class CreateStudentController(
    private val createStudent: CreateStudentUseCase,
    private val authService: AuthServiceUseCase
) {

    suspend fun execute(call: ApplicationCall) {
        try {
            val body = call.receive<CreateUserRequest>()

            if (body.firstName.isEmpty() || body.lastName.isEmpty() || body.email.isEmpty() || body.password.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Faltan campos requeridos"))
                return
            }

            val student = Student(
                firstName = body.firstName,
                middleName = body.middleName,
                lastName = body.lastName,
                secondLastName = body.secondLastName,
                email = body.email,
                phone = body.phone,
                password = body.password,
                registrationDate = LocalDateTime.now(),
                roleId = body.roleId
            )

            val savedUser = authService.register(user)

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