package tutors.application

import tutors.domain.ITutorRepository
import tutors.domain.entities.Tutor

class UpdateTutorUseCase(private val db: ITutorRepository) {
    
    suspend fun execute(tutor: Tutor) {
        if (tutor.tutorId == null || tutor.tutorId <= 0) {
            throw IllegalArgumentException("ID de tutor inválido")
        }
        
        if (tutor.userId <= 0) {
            throw IllegalArgumentException("El ID de usuario es inválido")
        }
        
        val existingTutor = db.getById(tutor.tutorId)
            ?: throw IllegalArgumentException("Tutor no encontrado")
        
        if (existingTutor.userId != tutor.userId) {
            throw IllegalArgumentException("No se puede cambiar el ID de usuario del tutor")
        }

        db.update(tutor)
    }
}