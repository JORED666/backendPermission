package students.application

import students.domain.IStudentRepository

class GetStudentsByTutorIdUseCase(private val db: IStudentRepository) {
    
    suspend fun execute(tutorId: Int): List<Map<String, Any?>> {
        return db.getStudentsByTutorId(tutorId)
    }
}