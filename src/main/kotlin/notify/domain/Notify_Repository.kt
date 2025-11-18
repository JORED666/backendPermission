package notify.domain

import notify.domain.entities.Notify
import notify.domain.dto.NotificationWithDetailsResponse

interface INotifyRepository {
    suspend fun save(notify: Notify): Notify
    suspend fun getById(notificationId: Int): Notify?
    suspend fun getByReceiverId(receiverId: Int): List<Notify>
    suspend fun getUnreadByReceiverId(receiverId: Int): List<Notify>
    suspend fun markAsRead(notificationId: Int): Unit
    suspend fun markAllAsReadByReceiver(receiverId: Int): Unit
    suspend fun getWithDetails(receiverId: Int): List<Map<String, Any?>>
    suspend fun getNotificationWithDetails(notificationId: Int): NotificationWithDetailsResponse?
}