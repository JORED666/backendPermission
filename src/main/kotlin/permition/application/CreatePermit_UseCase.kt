package permition.application

import permition.domain.PermitRepository
import permition.domain.entities.Permition
import java.time.LocalDateTime

class CreatePermitUseCase(private val db: PermitRepository) {
    
    suspend fun execute(permit: Permition): Permition {
        if (permit.description.isBlank()) {
            throw IllegalArgumentException("La descripción es obligatoria")
        }
        
        if (permit.startDate.isAfter(permit.endDate)) {
            throw IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin")
        }
        
        if (permit.endDate.isBefore(permit.startDate)) {
            throw IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio")
        }
        
        val permitWithDate = permit.copy(requestDate = LocalDateTime.now())
        
        return db.save(permitWithDate)
    }
}