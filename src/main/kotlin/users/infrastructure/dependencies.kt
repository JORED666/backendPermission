package users.infrastructure

import core.ConnMySQL
import users.infrastructure.adapters.MySQLUserRepository
import users.application.AuthServiceUseCase
import users.application.CreateUserUseCase
import users.application.GetAllUsersUseCase
import users.application.GetUserByIdUseCase
import users.application.UpdateUserUseCase
import users.application.DeleteUserUseCase
import users.infrastructure.controller.AuthController
import users.infrastructure.controller.CreateUserController
import users.infrastructure.controller.GetAllUsersController
import users.infrastructure.controller.GetUserByIdController
import users.infrastructure.controller.UpdateUserController
import users.infrastructure.controller.DeleteUserController

data class DependenciesUsers(
    val createUserController: CreateUserController,
    val getAllUsersController: GetAllUsersController,
    val getByIdUserController: GetUserByIdController,
    val updateUserController: UpdateUserController,
    val deleteUserController: DeleteUserController,
    val authController: AuthController
)

fun initUsers(conn: ConnMySQL): DependenciesUsers {
    val userRepository = MySQLUserRepository(conn)
    
    val authService = AuthServiceUseCase(userRepository)
    val createUserUseCase = CreateUserUseCase(userRepository)
    val getAllUsersUseCase = GetAllUsersUseCase(userRepository)
    val getUserByIdUseCase = GetUserByIdUseCase(userRepository)
    val updateUserUseCase = UpdateUserUseCase(userRepository)
    val deleteUserUseCase = DeleteUserUseCase(userRepository)

    return DependenciesUsers(
        createUserController = CreateUserController(createUserUseCase, authService),
        getAllUsersController = GetAllUsersController(getAllUsersUseCase),
        getByIdUserController = GetUserByIdController(getUserByIdUseCase),
        updateUserController = UpdateUserController(updateUserUseCase),
        deleteUserController = DeleteUserController(deleteUserUseCase),
        authController = AuthController(authService)
    )
}