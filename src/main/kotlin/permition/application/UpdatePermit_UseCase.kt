package permition.application

import permition.domain.PermitRepository
import permition.domain.entities.Permition
import permition.domain.entities.PermitStatus 

class UpdatePermitUseCase(private val db: PermitRepository) {
    
    suspend fun execute(permit: Permition) {
        if (permit.permitId == null) {
            throw IllegalArgumentException("El ID del permiso es requerido")
        }
        
        val existingPermit = db.getById(permit.permitId)
            ?: throw IllegalArgumentException("El permiso no existe")
        
        if (existingPermit.status != PermitStatus.PENDING && permit.status == PermitStatus.PENDING) {
            throw IllegalArgumentException("No se puede reactivar un permiso ya procesado")
        }

        if (permit.startDate.isAfter(permit.endDate)) {
            throw IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin")
        }
        
        db.update(permit)
    }
}