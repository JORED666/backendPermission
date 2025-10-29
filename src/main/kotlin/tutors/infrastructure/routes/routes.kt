package tutors.infrastructure.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import tutors.infrastructure.controller.*

fun Application.configureTutorRoutes(
    createTutorController: CreateTutorController,
    getAllTutorsController: GetAllTutorsController,
    getTutorByIdController: GetTutorByIdController,
    updateTutorController: UpdateTutorController,
    deleteTutorController: DeleteTutorController
) {
    routing {
        route("/api/tutors") {
            post {
                createTutorController.execute(call)
            }
            get {
                getAllTutorsController.execute(call)
            }
            get("/{id}") {
                getTutorByIdController.execute(call)
            }
            put("/{id}") {
                updateTutorController.execute(call)
            }
            delete("/{id}") {
                deleteTutorController.execute(call)
            }
        }
    }
}