package teachers.application

import teachers.domain.ITeacherRepository
import teachers.domain.entities.Teacher

class GetTeacherByIdUseCase(private val db: ITeacherRepository) {
    
    suspend fun execute(teacherId: Int): Teacher? {
        return db.getById(teacherId)
    }
}