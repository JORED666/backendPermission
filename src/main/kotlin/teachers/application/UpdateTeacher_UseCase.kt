package teachers.application

import teachers.domain.ITeacherRepository
import teachers.domain.entities.Teacher

class UpdateTeacherUseCase(private val db: ITeacherRepository) {
    
    suspend fun execute(teacher: Teacher) {
        if (teacher.teacherId == null) {
            throw IllegalArgumentException("El ID del teacher es requerido")
        }
        
        val existingTeacher = db.getById(teacher.teacherId)
            ?: throw IllegalArgumentException("El teacher no existe")
        
        if (existingTeacher.userId != teacher.userId) {
            throw IllegalArgumentException("No se puede modificar el ID de usuario asociado")
        }
        
        if (teacher.userId <= 0) {
            throw IllegalArgumentException("El ID de usuario es inválido")
        }
        
        db.update(teacher)
    }
}