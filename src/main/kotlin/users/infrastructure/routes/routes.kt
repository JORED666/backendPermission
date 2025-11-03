package users.infrastructure.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import users.infrastructure.controller.*

fun Application.configureUserRoutes(
    createUserController: CreateUserController,
    getAllUsersController: GetAllUsersController,
    getByIdUserController: GetUserByIdController,
    updateUserController: UpdateUserController,
    deleteUserController: DeleteUserController,
    authController: AuthController,
    googleOAuthController: GoogleOAuth_Controller,
    gitHubOAuthController: GitHubOAuth_Controller
) {
    routing {
        route("/api") {
            route("/users") {
                post {
                    createUserController.execute(call)
                }
                get {
                    getAllUsersController.execute(call)
                }
                get("/{id}") {
                    getByIdUserController.execute(call)
                }
                put("/{id}") {
                    updateUserController.execute(call)
                }
                delete("/{id}") {
                    deleteUserController.execute(call)
                }
            }

            route("/auth") {
                // Login tradicional
                post("/login") {
                    authController.execute(call)
                }
                
                // GOOGLE OAUTH
                get("/google") {
                    googleOAuthController.login(call)
                }
                get("/google/callback") {
                    googleOAuthController.callback(call)
                }
                
                // GITHUB OAUTH
                get("/github") {
                    gitHubOAuthController.login(call)
                }
                get("/github/callback") {
                    gitHubOAuthController.callback(call)
                }
            }
        }
    }
}