package tutors.application

import tutors.domain.ITutorRepository
import tutors.domain.entities.Tutor

class GetAllTutorsUseCase(private val db: ITutorRepository) {
    
    suspend fun execute(): List<Tutor> {
        return db.getAll()
    }
}