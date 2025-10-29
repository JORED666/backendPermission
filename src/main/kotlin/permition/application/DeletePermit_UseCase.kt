package permition.application

import permition.domain.PermitRepository
import permition.domain.entities.PermitStatus

class DeletePermitUseCase(private val db: PermitRepository) {

    suspend fun execute(permitId: Int) {
        val existingPermit =
                db.getById(permitId) ?: throw IllegalArgumentException("El permiso no existe")

        if (existingPermit.status != PermitStatus.PENDING) {
            throw IllegalArgumentException("Solo se pueden eliminar permisos pendientes")
        }

        db.delete(permitId)
    }
}
