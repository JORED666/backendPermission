package notify.domain.dto

import kotlinx.serialization.Serializable
import notify.domain.entities.Notify

// Requests
@Serializable
data class CreateNotificationRequest(
    val senderId: Int,
    val receiverId: Int,
    val type: String,
    val message: String,
    val relatedPermitId: Int?
)

@Serializable
data class MarkAsReadRequest(
    val notificationId: Int
)

// Response components
@Serializable
data class SenderInfoResponse(
    val userId: Int,
    val nombreCompleto: String,
    val email: String
)

@Serializable
data class PermitInfoResponse(
    val permitId: Int,
    val matricula: String,  
    val motivo: String,
    val estado: String
)

@Serializable
data class NotificationWithDetailsResponse(
    val notificationId: Int,
    val senderId: Int,
    val receiverId: Int,
    val tipo: String,
    val mensaje: String,
    val relatedPermitId: Int?,
    val leido: Boolean,
    val fechaCreacion: String,
    val informacionRemitente: SenderInfoResponse,
    val informacionPermiso: PermitInfoResponse?
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): NotificationWithDetailsResponse {
            return NotificationWithDetailsResponse(
                notificationId = data["notification_id"] as Int,
                senderId = data["sender_id"] as Int,
                receiverId = data["receiver_id"] as Int,
                tipo = data["type"] as String,
                mensaje = data["message"] as String,
                relatedPermitId = data["related_permit_id"] as? Int,
                leido = data["is_read"] as Boolean,
                fechaCreacion = data["created_at"].toString(),
                informacionRemitente = SenderInfoResponse(
                    userId = data["sender_id"] as Int,
                    nombreCompleto = (data["sender_name"] as String).trim(),
                    email = data["sender_email"] as String
                ),
                informacionPermiso = if (data["permit_id"] != null) {
                    PermitInfoResponse(
                        permitId = data["permit_id"] as Int,
                        matricula = data["student_matricula"] as String,  
                        motivo = data["permit_reason"] as String,
                        estado = data["permit_status"] as String
                    )
                } else null
            )
        }
    }
}

@Serializable
data class NotificationResponse(
    val notificationId: Int?,
    val senderId: Int,
    val receiverId: Int,
    val tipo: String,
    val mensaje: String,
    val relatedPermitId: Int?,
    val leido: Boolean,
    val fechaCreacion: String?
) {
    companion object {
        fun fromNotify(notify: Notify): NotificationResponse {
            return NotificationResponse(
                notificationId = notify.notificationId,
                senderId = notify.senderId,
                receiverId = notify.receiverId,
                tipo = notify.type,
                mensaje = notify.message,
                relatedPermitId = notify.relatedPermitId,
                leido = notify.isRead,
                fechaCreacion = notify.createdAt?.toString() ?: java.time.LocalDateTime.now().toString()
            )
        }
    }
}

// Main responses
@Serializable
data class NotificationListResponse(
    val notifications: List<NotificationWithDetailsResponse>,
    val total: Int,
    val unread: Int
)

@Serializable
data class CreateNotificationResponse(
    val message: String,
    val notification: NotificationResponse
)

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)

// WebSocket message
@Serializable
data class WebSocketNotification(
    val type: String = "notification",
    val data: NotificationWithDetailsResponse
)