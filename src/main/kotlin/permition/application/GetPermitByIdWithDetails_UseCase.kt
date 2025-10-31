package permition.application

import permition.domain.PermitRepository
import permition.domain.entities.PermitWithDetails

class GetPermitByIdWithDetailsUseCase(private val db: PermitRepository) {
    
    suspend fun execute(permitId: Int): PermitWithDetails? {
        return db.getByIdWithDetails(permitId)
    }
}