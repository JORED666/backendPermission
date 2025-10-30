package teachers.application

import teachers.domain.ITeacherRepository
import teachers.domain.entities.TeacherWithDetails

class GetTeacherByIdWithDetailsUseCase(private val db: ITeacherRepository) {
    
    suspend fun execute(teacherId: Int): TeacherWithDetails? {
        return db.getByIdWithDetails(teacherId)
    }
}