package students.application

import students.domain.IStudentRepository

class DeleteStudentUseCase(private val db: IStudentRepository) {
    
    suspend fun execute(studentId: Int) {
        if (studentId <= 0) {
            throw IllegalArgumentException("El ID del estudiante es inválido")
        }
        
        val existing = db.getById(studentId)
        if (existing == null) {
            throw IllegalArgumentException("Estudiante no encontrado")
        }
        
        db.delete(studentId)
    }
}