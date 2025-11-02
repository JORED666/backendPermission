package permitsTeacher.application

import permitsTeacher.domain.IPermitTeacherRepository

class DeletePermitTeacherUseCase(private val db: IPermitTeacherRepository) {
    
    suspend fun execute(permitId: Int, teacherId: Int) {
        if (permitId <= 0) {
            throw IllegalArgumentException("El ID del permiso es inválido")
        }
        
        if (teacherId <= 0) {
            throw IllegalArgumentException("El ID del profesor es inválido")
        }
        
        val existing = db.getByPermitAndTeacher(permitId, teacherId)
        if (existing == null) {
            throw IllegalArgumentException("La asignación de permiso no existe")
        }
        
        db.delete(permitId, teacherId)
    }
}