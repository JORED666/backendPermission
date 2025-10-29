package permition.application

import permition.domain.PermitRepository
import permition.domain.entities.Permition

class GetAllPermitsUseCase(private val db: PermitRepository) {
    
    suspend fun execute(): List<Permition> {
        return db.getAll()
    }
}