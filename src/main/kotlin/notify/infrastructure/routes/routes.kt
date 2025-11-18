package notify.infrastructure.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import notify.infrastructure.controllers.*

fun Application.configureNotificationRoutes(
    getNotificationsController: GetNotificationsController,
    markAsReadController: MarkAsReadController
) {
    routing {
        route("/api") {
            route("/notifications") {
                // Obtener notificaciones de un usuario
                get("/user/{userId}") {
                    getNotificationsController.execute(call)
                }
                
                // Marcar una notificación como leída
                put("/{id}/read") {
                    markAsReadController.executeOne(call)
                }
                
                // Marcar todas las notificaciones de un usuario como leídas
                put ("/user/{userId}/read-all") {
                    markAsReadController.executeAll(call)
                }
            }
        }
    }
}