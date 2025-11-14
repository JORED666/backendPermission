package students.application

import students.domain.IStudentRepository

class GetStudentByUserIdUseCase(private val db: IStudentRepository) {
    
    suspend fun execute(userId: Int): Map<String, Any?>? {
        if (userId <= 0) {
            throw IllegalArgumentException("El ID del usuario es inválido")
        }
        
        val student = db.getByUserId(userId)
            ?: throw IllegalArgumentException("No se encontró estudiante para el usuario con ID: $userId")
        
        return db.getByIdWithDetails(student.studentId!!)
    }
}