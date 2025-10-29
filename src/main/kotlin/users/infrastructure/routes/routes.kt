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
    authController: AuthController
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
                post("/login") {
                    authController.execute(call)
                }
            }
        }
    }
}