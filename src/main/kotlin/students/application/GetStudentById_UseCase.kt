package students.application

import students.domain.IStudentRepository

class GetStudentByIdWithDetailsUseCase(private val db: IStudentRepository) {
    
    suspend fun execute(studentId: Int): Map<String, Any?>? {
        if (studentId <= 0) {
            throw IllegalArgumentException("El ID del estudiante es inválido")
        }
        
        return db.getByIdWithDetails(studentId)
    }
}