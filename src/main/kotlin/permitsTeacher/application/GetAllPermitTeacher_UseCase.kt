package permitsTeacher.application

import permitsTeacher.domain.IPermitTeacherRepository

class GetAllPermitTeacherUseCase(private val db: IPermitTeacherRepository) {
    
    suspend fun execute(): List<Map<String, Any?>> {
        return db.getAllWithDetails()
    }
}