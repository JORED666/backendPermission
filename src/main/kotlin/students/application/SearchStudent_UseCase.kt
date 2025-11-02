package students.application

import students.domain.IStudentRepository

class SearchStudentUseCase(private val db: IStudentRepository) {
    
    suspend fun execute(query: String): List<Map<String, Any?>> {
        if (query.isBlank()) {
            throw IllegalArgumentException("El término de búsqueda no puede estar vacío")
        }
        
        return db.search(query.trim())
    }
}