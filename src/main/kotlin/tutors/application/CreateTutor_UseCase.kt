package tutors.application

import tutors.domain.ITutorRepository
import tutors.domain.entities.Tutor

class CreateTutorUseCase(private val db: ITutorRepository) {
    
    suspend fun execute(tutor: Tutor): Tutor {
        if (tutor.userId <= 0) {
            throw IllegalArgumentException("El ID de usuario es inválido")
        }
        
        val existingTutor = db.getByUserId(tutor.userId)
        if (existingTutor != null) {
            throw IllegalArgumentException("Este usuario ya es un tutor")
        }
        
        return db.save(tutor)
    }
}