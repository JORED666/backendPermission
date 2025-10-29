package tutors.application

import tutors.domain.ITutorRepository
import tutors.domain.entities.TutorWithDetails

class GetTutorByIdWithDetailsUseCase(private val db: ITutorRepository) {
    
    suspend fun execute(id: Int): TutorWithDetails? {
        if (id <= 0) {
            throw IllegalArgumentException("ID inválido")
        }
        
        return db.getByIdWithDetails(id)
    }
}