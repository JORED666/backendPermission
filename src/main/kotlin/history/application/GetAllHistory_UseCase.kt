package history.application

import history.domain.IHistoryRepository

class GetAllHistoryUseCase(private val db: IHistoryRepository) {
    
    suspend fun execute(): List<Map<String, Any?>> {
        return db.getAllWithDetails()
    }
}