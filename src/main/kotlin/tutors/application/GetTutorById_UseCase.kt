package tutors.application

import tutors.domain.ITutorRepository
import tutors.domain.entities.Tutor

class GetTutorByIdUseCase(private val db: ITutorRepository) {
    
    suspend fun execute(id: Int): Tutor? {
        if (id <= 0) {
            throw IllegalArgumentException("ID inválido")
        }
        
        return db.getById(id)
    }
}