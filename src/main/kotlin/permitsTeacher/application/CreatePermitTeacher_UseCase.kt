package permitsTeacher.application

import permitsTeacher.domain.IPermitTeacherRepository
import permitsTeacher.domain.entities.PermitTeacher

class CreatePermitTeacherUseCase(private val db: IPermitTeacherRepository) {
    
    suspend fun execute(permitTeacher: PermitTeacher): PermitTeacher {
        if (permitTeacher.permitId <= 0) {
            throw IllegalArgumentException("El ID del permiso es inválido")
        }
        
        if (permitTeacher.teacherId <= 0) {
            throw IllegalArgumentException("El ID del profesor es inválido")
        }
        
        val existing = db.getByPermitAndTeacher(permitTeacher.permitId, permitTeacher.teacherId)
        if (existing != null) {
            throw IllegalArgumentException("Este permiso ya está asignado a este profesor")
        }
        
        return db.save(permitTeacher)
    }
}