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
        connections.getOrPut(userId) { ConcurrentHashMap.newKeySet() }.add(session)
        
        val totalConnections = getTotalConnections()
        val userSessions = connections[userId]?.size ?: 0
        
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("🔌 User $userId connected")
        println("   👤 Sessions for this user: $userSessions")
        println("   🌐 Total connections: $totalConnections")
        println("   👥 Total users: ${getTotalUsers()}")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }
    
    fun removeConnection(userId: Int, session: WebSocketSession) {
        val sessionsBeforeRemoval = connections[userId]?.size ?: 0
        connections[userId]?.remove(session)
        
        if (connections[userId]?.isEmpty() == true) {
            connections.remove(userId)
        }
        
        val sessionsAfterRemoval = connections[userId]?.size ?: 0
        val totalConnections = getTotalConnections()
        
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("🔌 User $userId disconnected")
        println("   👤 Sessions: $sessionsBeforeRemoval -> $sessionsAfterRemoval")
        println("   🌐 Total connections: $totalConnections")
        println("   👥 Total users: ${getTotalUsers()}")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }
    
    suspend fun sendNotificationToUser(userId: Int, notification: Notify) {
        val sessions = connections[userId]
        
        if (sessions == null || sessions.isEmpty()) {
            println("⚠️ No active sessions for user $userId")
            return
        }
        
        val notificationResponse = NotificationResponse.fromNotify(notification)
        val message = Json.encodeToString(notificationResponse)
        
        println("📡 Sending notification to user $userId (${sessions.size} sessions)")
        
        val sessionsToRemove = mutableSetOf<WebSocketSession>()
        var successCount = 0
        
        sessions.forEach { session ->
            try {
                session.send(Frame.Text(message))
                successCount++
                println("   ✅ Sent to session ${session.hashCode()}")
            } catch (e: Exception) {
                println("   ❌ Failed to send to session ${session.hashCode()}: ${e.message}")
                sessionsToRemove.add(session)
            }
        }
        
        sessionsToRemove.forEach { session ->
            removeConnection(userId, session)
        }
        
        println("📊 Notification delivery: $successCount/${sessions.size} successful")
    }
    
    suspend fun sendNotificationWithDetails(userId: Int, notificationDetails: NotificationWithDetailsResponse) {
        val sessions = connections[userId]
        
        if (sessions == null || sessions.isEmpty()) {
            println("⚠️ No active sessions for user $userId")
            return
        }
        
        val wsNotification = WebSocketNotification(
            type = "notification",
            data = notificationDetails
        )
        
        val message = Json.encodeToString(wsNotification)
        
        println("📡 Sending detailed notification to user $userId (${sessions.size} sessions)")
        
        val sessionsToRemove = mutableSetOf<WebSocketSession>()
        var successCount = 0
        
        sessions.forEach { session ->
            try {
                session.send(Frame.Text(message))
                successCount++
                println("   ✅ Sent to session ${session.hashCode()}")
            } catch (e: Exception) {
                println("   ❌ Failed to send to session ${session.hashCode()}: ${e.message}")
                sessionsToRemove.add(session)
            }
        }
        
        sessionsToRemove.forEach { session ->
            removeConnection(userId, session)
        }
        
        println("📊 Notification delivery: $successCount/${sessions.size} successful")
    }
    
    fun getConnectedUsers(): Set<Int> {
        return connections.keys.toSet()
    }
    
    fun isUserConnected(userId: Int): Boolean {
        return connections.containsKey(userId) && connections[userId]?.isNotEmpty() == true
    }
    
    private fun getTotalConnections(): Int {
        return connections.values.sumOf { it.size }
    }
    
    private fun getTotalUsers(): Int {
        return connections.size
    }
    
    fun getConnectionStats(): Map<String, Any> {
        return mapOf(
            "totalConnections" to getTotalConnections(),
            "totalUsers" to getTotalUsers(),
            "usersWithMultipleSessions" to connections.count { it.value.size > 1 },
            "userConnections" to connections.mapValues { it.value.size }
        )
    }
}