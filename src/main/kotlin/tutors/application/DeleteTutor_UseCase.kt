package tutors.application

import tutors.domain.ITutorRepository

class DeleteTutorUseCase(private val db: ITutorRepository) {
    
    suspend fun execute(id: Int) {
        if (id <= 0) {
            throw IllegalArgumentException("ID inválido")
        }
        
        db.delete(id)
    }
}