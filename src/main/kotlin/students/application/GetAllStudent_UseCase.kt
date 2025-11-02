package students.application

import students.domain.IStudentRepository

class GetAllStudentsWithDetailsUseCase(private val db: IStudentRepository) {
    
    suspend fun execute(): List<Map<String, Any?>> {
        return db.getAllWithDetails()
    }
}