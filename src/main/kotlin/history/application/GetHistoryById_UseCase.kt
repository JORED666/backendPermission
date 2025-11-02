package history.application

import history.domain.IHistoryRepository

class GetHistoryByIdUseCase(private val db: IHistoryRepository) {
    
    suspend fun execute(historyId: Int): Map<String, Any?>? {
        if (historyId <= 0) {
            throw IllegalArgumentException("El ID del historial es inválido")
        }
        
        return db.getByIdWithDetails(historyId)
    }
}