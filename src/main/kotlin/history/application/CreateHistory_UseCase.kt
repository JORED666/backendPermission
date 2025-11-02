package history.application

import history.domain.IHistoryRepository
import history.domain.entities.History

class CreateHistoryUseCase(private val db: IHistoryRepository) {
    
    suspend fun execute(history: History): History {
        if (history.permitId <= 0) {
            throw IllegalArgumentException("El ID del permiso es inválido")
        }
        
        if (history.studentId <= 0) {
            throw IllegalArgumentException("El ID del estudiante es inválido")
        }
        
        if (history.reason.isBlank()) {
            throw IllegalArgumentException("El motivo es requerido")
        }
        
        val validReasons = listOf("Family", "Health", "Economic", "Academic Events", 
                                  "Sports", "Pregnancy", "Accidents", "Addictions", 
                                  "Personal Procedures", "Other")
        if (!validReasons.contains(history.reason)) {
            throw IllegalArgumentException("Motivo inválido")
        }
        
        val validStatuses = listOf("pending", "approved", "rejected")
        if (!validStatuses.contains(history.status)) {
            throw IllegalArgumentException("Estado inválido")
        }
        
        if (history.startDate != null && history.endDate != null) {
            if (history.endDate.isBefore(history.startDate)) {
                throw IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial")
            }
        }
        
        return db.save(history)
    }
}