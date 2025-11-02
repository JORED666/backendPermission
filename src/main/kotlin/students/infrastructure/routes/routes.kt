package students.infrastructure.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import students.infrastructure.controllers.*

fun Application.configureStudentRoutes(
    createStudentController: CreateStudentController,
    getAllStudentController: GetAllStudentController,
    getStudentByIdController: GetStudentByIdController,
    searchStudentController: SearchStudentController,
    updateStudentController: UpdateStudentController,
    deleteStudentController: DeleteStudentController
) {
    routing {
        route("/api") {
            route("/students") {
                post {
                    createStudentController.execute(call)
                }
                
                get {
                    getAllStudentController.execute(call)
                }
                
                get("/search") {
                    searchStudentController.execute(call)
                }
                
                get("/{id}") {
                    getStudentByIdController.execute(call)
                }
                
                put("/{id}") {
                    updateStudentController.execute(call)
                }
                
                delete("/{id}") {
                    deleteStudentController.execute(call)
                }
            }
        }
    }
}