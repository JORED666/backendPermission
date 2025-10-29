package permition.application

import permition.domain.PermitRepository
import permition.domain.entities.Permition

class GetPermitByIdUseCase(private val db: PermitRepository) {
    
    suspend fun execute(permitId: Int): Permition? {
        return db.getById(permitId)
    }
}