package users.infrastructure

import core.ConnMySQL
import users.infrastructure.adapters.MySQLUserRepository
import tutors.infrastructure.adapters.MySQLTutorRepository  
import users.application.*
import users.infrastructure.controller.*

data class DependenciesUsers(
    val createUserController: CreateUserController,
    val getAllUsersController: GetAllUsersController,
    val getByIdUserController: GetUserByIdController,
    val updateUserController: UpdateUserController,
    val deleteUserController: DeleteUserController,
    val authController: AuthController,
    val googleOAuthController: GoogleOAuth_Controller,
    val gitHubOAuthController: GitHubOAuth_Controller
)

fun initUsers(
    conn: ConnMySQL,
    googleClientId: String,
    googleClientSecret: String,
    googleRedirectUrl: String,
    githubClientId: String,
    githubClientSecret: String,
    githubRedirectUrl: String,
    frontendUrl: String = "http://localhost:4200"
): DependenciesUsers {
    
    val userRepository = MySQLUserRepository(conn)
    val tutorRepository = MySQLTutorRepository(conn)  
    
    val authService = AuthServiceUseCase(userRepository, tutorRepository)  
    val oauthUseCase = OAuthUseCase(userRepository)
    val createUserUseCase = CreateUserUseCase(userRepository)
    val getAllUsersUseCase = GetAllUsersUseCase(userRepository)
    val getUserByIdUseCase = GetUserByIdUseCase(userRepository)
    val updateUserUseCase = UpdateUserUseCase(userRepository)
    val deleteUserUseCase = DeleteUserUseCase(userRepository)

    return DependenciesUsers(
        createUserController = CreateUserController(createUserUseCase, authService, userRepository),
        getAllUsersController = GetAllUsersController(getAllUsersUseCase),
        getByIdUserController = GetUserByIdController(getUserByIdUseCase),
        updateUserController = UpdateUserController(updateUserUseCase),
        deleteUserController = DeleteUserController(deleteUserUseCase),
        authController = AuthController(authService),
        googleOAuthController = GoogleOAuth_Controller(
            oauthUseCase = oauthUseCase,
            clientId = googleClientId,
            clientSecret = googleClientSecret,
            redirectUri = googleRedirectUrl,
            frontendUrl = frontendUrl
        ),
        gitHubOAuthController = GitHubOAuth_Controller(
            oauthUseCase = oauthUseCase,
            clientId = githubClientId,
            clientSecret = githubClientSecret,
            redirectUri = githubRedirectUrl,
            frontendUrl = frontendUrl
        )
    )
}