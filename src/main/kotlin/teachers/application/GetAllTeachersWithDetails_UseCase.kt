package teachers.application

import teachers.domain.ITeacherRepository
import teachers.domain.entities.TeacherWithDetails

class GetAllTeachersWithDetailsUseCase(private val db: ITeacherRepository) {
    
    suspend fun execute(): List<TeacherWithDetails> {
        return db.getAllWithDetails()
    }
}