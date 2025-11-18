package permition.application

import permition.domain.PermitRepository

class UpdatePermitDocumentUrlUseCase(private val db: PermitRepository) {
    
    suspend fun execute(permitId: Int, documentUrl: String) {
        if (documentUrl.isBlank()) {
            throw IllegalArgumentException("La URL del documento no puede estar vacía")
        }
        
        val permit = db.getById(permitId)
            ?: throw IllegalArgumentException("El permiso no existe")
        
        db.updatePermitDocumentUrl(permitId, documentUrl)
    }
}