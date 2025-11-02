package history.application

import history.domain.IHistoryRepository

class UpdateHistoryStatusUseCase(private val db: IHistoryRepository) {
    
    suspend fun execute(historyId: Int, status: String) {
        if (historyId <= 0) {
            throw IllegalArgumentException("El ID del historial es inválido")
        }
        
        if (status.isBlank()) {
            throw IllegalArgumentException("El estado es requerido")
        }
        
        val validStatuses = listOf("pending", "approved", "rejected")
        if (!validStatuses.contains(status)) {
            throw IllegalArgumentException("Estado inválido. Use: pending, approved o rejected")
        }
        
        val existing = db.getById(historyId)
        if (existing == null) {
            throw IllegalArgumentException("Historial no encontrado")
        }
        
        db.updateStatus(historyId, status)
    }
}