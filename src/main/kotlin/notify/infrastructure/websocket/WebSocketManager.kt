package notify.infrastructure.websocket

import io.ktor.websocket.*
import notify.domain.entities.Notify
import notify.domain.dto.NotificationWithDetailsResponse
import notify.domain.dto.NotificationResponse
import notify.domain.dto.WebSocketNotification
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class WebSocketManager {
    private val connections = ConcurrentHashMap<Int, MutableSet<WebSocketSession>>()
    
    fun addConnection(userId: Int, session: WebSocketSession) {
        connections.getOrPut(userId) { mutableSetOf() }.add(session)
        println("User $userId connected. Total connections: ${connections[userId]?.size}")
    }
    
    fun removeConnection(userId: Int, session: WebSocketSession) {
        connections[userId]?.remove(session)
        if (connections[userId]?.isEmpty() == true) {
            connections.remove(userId)
        }
        println("User $userId disconnected. Remaining connections: ${connections[userId]?.size ?: 0}")
    }
    
    suspend fun sendNotificationToUser(userId: Int, notification: Notify) {
        val sessions = connections[userId] ?: return
        
        // Convertir Notify a NotificationResponse que SÍ es serializable
        val notificationResponse = NotificationResponse.fromNotify(notification)
        val message = Json.encodeToString(notificationResponse)
        
        sessions.forEach { session ->
            try {
                session.send(Frame.Text(message))
                println("Notification sent to user $userId")
            } catch (e: Exception) {
                println("Error sending notification to user $userId: ${e.message}")
                removeConnection(userId, session)
            }
        }
    }
    
    suspend fun sendNotificationWithDetails(userId: Int, notificationDetails: NotificationWithDetailsResponse) {
        val sessions = connections[userId] ?: return
        
        val wsNotification = WebSocketNotification(
            type = "notification",
            data = notificationDetails
        )
        
        val message = Json.encodeToString(wsNotification)
        
        sessions.forEach { session ->
            try {
                session.send(Frame.Text(message))
                println("Detailed notification sent to user $userId")
            } catch (e: Exception) {
                println("Error sending detailed notification to user $userId: ${e.message}")
                removeConnection(userId, session)
            }
        }
    }
    
    fun getConnectedUsers(): Set<Int> {
        return connections.keys.toSet()
    }
    
    fun isUserConnected(userId: Int): Boolean {
        return connections.containsKey(userId) && connections[userId]?.isNotEmpty() == true
    }
}