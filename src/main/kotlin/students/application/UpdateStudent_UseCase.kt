package students.application

import students.domain.IStudentRepository
import students.domain.entities.Student

class UpdateStudentUseCase(private val db: IStudentRepository) {
    
    suspend fun getExistingStudent(studentId: Int): Student? {
        return db.getById(studentId)
    }
    
    suspend fun execute(student: Student) {
        if (student.studentId == null || student.studentId <= 0) {
            throw IllegalArgumentException("El ID del estudiante es inválido")
        }
        
        if (student.userId <= 0) {
            throw IllegalArgumentException("El ID de usuario es inválido")
        }
        
        if (student.enrollmentNumber != null && student.enrollmentNumber.isBlank()) {
            throw IllegalArgumentException("La matrícula no puede estar vacía")
        }
        
        val existing = db.getById(student.studentId)
        if (existing == null) {
            throw IllegalArgumentException("Estudiante no encontrado")
        }
        
        if (student.enrollmentNumber != null && 
            student.enrollmentNumber != existing.enrollmentNumber) {
            val existingByEnrollment = db.getByEnrollmentNumber(student.enrollmentNumber)
            if (existingByEnrollment != null && 
                existingByEnrollment.studentId != student.studentId) {
                throw IllegalArgumentException("Esta matrícula ya está registrada por otro estudiante")
            }
        }
        
        db.update(student)
    }
}