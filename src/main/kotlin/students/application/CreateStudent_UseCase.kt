package students.application

import students.domain.IStudentRepository
import students.domain.entities.Student

class CreateStudentUseCase(private val db: IStudentRepository) {
    
    suspend fun execute(student: Student): Student {
        if (student.userId <= 0) {
            throw IllegalArgumentException("El ID de usuario es inválido")
        }
        
        if (student.enrollmentNumber.isNullOrBlank()) {
            throw IllegalArgumentException("La matrícula es requerida")
        }
        
        val existingByUser = db.getByUserId(student.userId)
        if (existingByUser != null) {
            throw IllegalArgumentException("Este usuario ya está registrado como estudiante")
        }
        
        val existingByEnrollment = db.getByEnrollmentNumber(student.enrollmentNumber)
        if (existingByEnrollment != null) {
            throw IllegalArgumentException("Esta matrícula ya está registrada")
        }
        
        return db.save(student)
    }
}