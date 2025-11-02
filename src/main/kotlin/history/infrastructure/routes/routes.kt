package history.infrastructure.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import history.infrastructure.controllers.*

fun Application.configureHistoryRoutes(
    createHistoryController: CreateHistoryController,
    getAllHistoryController: GetAllHistoryController,
    getHistoryByIdController: GetHistoryByIdController,
    getHistoryByStudentController: GetHistoryByStudentController,
    updateHistoryStatusController: UpdateHistoryStatusController
) {
    routing {
        route("/api") {
            route("/history") {
                post {
                    createHistoryController.execute(call)
                }
                
                get {
                    getAllHistoryController.execute(call)
                }
                
                get("/{id}") {
                    getHistoryByIdController.execute(call)
                }
                
                get("/student/{studentId}") {
                    getHistoryByStudentController.execute(call)
                }
                
                patch("/{id}/status") {
                    updateHistoryStatusController.execute(call)
                }
            }
        }
    }
}