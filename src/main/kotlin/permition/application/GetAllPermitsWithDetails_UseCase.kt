package permition.application

import permition.domain.PermitRepository
import permition.domain.entities.PermitWithDetails

class GetAllPermitsWithDetailsUseCase(private val db: PermitRepository) {
    
    suspend fun execute(): List<PermitWithDetails> {
        return db.getAllWithDetails()
    }
}