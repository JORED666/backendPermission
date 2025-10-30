package teachers.infrastructure.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import teachers.infrastructure.controllers.*

fun Application.configureTeacherRoutes(
    createTeacherController: CreateTeacherController,
    getAllTeacherController: GetAllTeacherController,
    getTeacherByIdController: GetTeacherByIdController,
    updateTeacherController: UpdateTeacherController,
    deleteTeacherController: DeleteTeacherController
) {
    routing {
        route("/api") {
            route("/teachers") {
                post {
                    createTeacherController.execute(call)
                }
                
                get {
                    getAllTeacherController.execute(call)
                }
                
                get("/{id}") {
                    getTeacherByIdController.execute(call)
                }
                
                put("/{id}") {
                    updateTeacherController.execute(call)
                }
                
                delete("/{id}") {
                    deleteTeacherController.execute(call)
                }
            }
        }
    }
}