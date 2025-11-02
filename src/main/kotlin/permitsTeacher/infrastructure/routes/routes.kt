package permitsTeacher.infrastructure.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import permitsTeacher.infrastructure.controllers.*

fun Application.configurePermitTeacherRoutes(
    createPermitTeacherController: CreatePermitTeacherController,
    getAllPermitTeacherController: GetAllPermitTeacherController,
    getPermitTeacherByIdController: GetPermitTeacherByIdController,
    deletePermitTeacherController: DeletePermitTeacherController
) {
    routing {
        route("/api") {
            route("/permits-teachers") {
                post {
                    createPermitTeacherController.execute(call)
                }
                
                get {
                    getAllPermitTeacherController.execute(call)
                }
                
                get("/{type}/{id}") {
                    getPermitTeacherByIdController.execute(call)
                }
                
                delete("/{permitId}/{teacherId}") {
                    deletePermitTeacherController.execute(call)
                }
            }
        }
    }
}