package notify.infrastructure.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import notify.application.GetNotificationsByUserUseCase
import notify.domain.dto.*

class GetNotificationsController(
    private val getNotifications: GetNotificationsByUserUseCase
) {
    
    suspend fun execute(call: ApplicationCall) {
        try {
            val userId = call.parameters["userId"]?.toIntOrNull()

            if (userId == null || userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID de usuario inválido"))
                return
            }

            val notificationsData = getNotifications.execute(userId)
            val unreadCount = getNotifications.getUnreadCount(userId)
            
            val notifications = notificationsData.map { 
                NotificationWithDetailsResponse.fromMap(it) 
            }

            call.respond(HttpStatusCode.OK, NotificationListResponse(
                notifications = notifications,
                total = notifications.size,
                unread = unreadCount
            ))
        } catch (error: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error.message ?: "Error de validación")
            )
        } catch (error: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(error.message ?: "Error desconocido")
            )
        }
    }
}