package teachers.application

import teachers.domain.ITeacherRepository
import teachers.domain.entities.Teacher

class GetAllTeacherUseCase(private val db: ITeacherRepository) {
    
    suspend fun execute(): List<Teacher> {
        return db.getAll()
    }
}