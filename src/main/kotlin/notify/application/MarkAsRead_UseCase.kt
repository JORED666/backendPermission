package notify.application

import notify.domain.INotifyRepository

class MarkAsReadUseCase(private val db: INotifyRepository) {
    
    suspend fun execute(notificationId: Int) {
        if (notificationId <= 0) {
            throw IllegalArgumentException("El ID de la notificación es inválido")
        }
        
        val existing = db.getById(notificationId)
        if (existing == null) {
            throw IllegalArgumentException("Notificación no encontrada")
        }
        
        db.markAsRead(notificationId)
    }
    
    suspend fun markAllAsRead(userId: Int) {
        if (userId <= 0) {
            throw IllegalArgumentException("El ID del usuario es inválido")
        }
        
        db.markAllAsReadByReceiver(userId)
    }
}