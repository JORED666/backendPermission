package teachers.application

import teachers.domain.ITeacherRepository
import teachers.domain.entities.Teacher

class CreateTeacherUseCase(private val db: ITeacherRepository) {
    
    suspend fun execute(teacher: Teacher): Teacher {
        if (teacher.userId <= 0) {
            throw IllegalArgumentException("El ID de usuario es inválido")
        }
        
        val existingTeacher = db.getByUserId(teacher.userId)
        if (existingTeacher != null) {
            throw IllegalArgumentException("Este usuario ya está registrado como teacher")
        }
        
        return db.save(teacher)
    }
}