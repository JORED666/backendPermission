package notify.application

import notify.domain.INotifyRepository

class GetNotificationsByUserUseCase(private val db: INotifyRepository) {
    
    suspend fun execute(userId: Int): List<Map<String, Any?>> {
        if (userId <= 0) {
            throw IllegalArgumentException("El ID del usuario es inválido")
        }
        
        return db.getWithDetails(userId)
    }
    
    suspend fun getUnreadCount(userId: Int): Int {
        if (userId <= 0) {
            throw IllegalArgumentException("El ID del usuario es inválido")
        }
        
        return db.getUnreadByReceiverId(userId).size
    }
}