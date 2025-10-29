package tutors.application

import tutors.domain.ITutorRepository
import tutors.domain.entities.TutorWithDetails

class GetAllTutorsWithDetailsUseCase(private val db: ITutorRepository) {
    
    suspend fun execute(): List<TutorWithDetails> {
        return db.getAllWithDetails()
    }
}