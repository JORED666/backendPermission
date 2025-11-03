package notify.application

import notify.domain.INotifyRepository
import notify.domain.entities.Notify

class CreateNotificationUseCase(private val db: INotifyRepository) {
    
    suspend fun execute(notify: Notify): Notify {
        if (notify.senderId <= 0) {
            throw IllegalArgumentException("El ID del remitente es inválido")
        }
        
        if (notify.receiverId <= 0) {
            throw IllegalArgumentException("El ID del destinatario es inválido")
        }
        
        if (notify.message.isBlank()) {
            throw IllegalArgumentException("El mensaje es requerido")
        }
        
        val validTypes = listOf("new_permit", "permit_status", "permit_assigned")
        if (!validTypes.contains(notify.type)) {
            throw IllegalArgumentException("Tipo de notificación inválido")
        }
        
        return db.save(notify)
    }
}