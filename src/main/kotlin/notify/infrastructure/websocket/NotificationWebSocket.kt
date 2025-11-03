package notify.infrastructure.websocket

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.channels.consumeEach

fun Application.configureNotificationWebSocket(webSocketManager: WebSocketManager) {
    routing {
        webSocket("/ws/notifications/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            
            if (userId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid user ID"))
                return@webSocket
            }
            
            webSocketManager.addConnection(userId, this)
            
            try {
                send(Frame.Text("""{"type": "connected", "message": "Connected to notifications"}"""))
                
                incoming.consumeEach { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("Received from user $userId: $text")
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                println("WebSocket error for user $userId: ${e.message}")
            } finally {
                webSocketManager.removeConnection(userId, this)
            }
        }
    }
}