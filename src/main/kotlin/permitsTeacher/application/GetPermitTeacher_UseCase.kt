package permitsTeacher.application

import permitsTeacher.domain.IPermitTeacherRepository

class GetPermitTeacherUseCase(private val db: IPermitTeacherRepository) {
    
    suspend fun executeByPermitId(permitId: Int): List<Map<String, Any?>> {
        if (permitId <= 0) {
            throw IllegalArgumentException("El ID del permiso es inválido")
        }
        
        return db.getByPermitIdWithDetails(permitId)
    }
    
    suspend fun executeByTeacherId(teacherId: Int): List<Map<String, Any?>> {
        if (teacherId <= 0) {
            throw IllegalArgumentException("El ID del profesor es inválido")
        }
        
        return db.getByTeacherIdWithDetails(teacherId)
    }
}