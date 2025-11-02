package history.application

import history.domain.IHistoryRepository

class GetHistoryByStudentUseCase(private val db: IHistoryRepository) {
    
    suspend fun execute(studentId: Int): List<Map<String, Any?>> {
        if (studentId <= 0) {
            throw IllegalArgumentException("El ID del estudiante es inválido")
        }
        
        return db.getByStudentIdWithDetails(studentId)
    }
}