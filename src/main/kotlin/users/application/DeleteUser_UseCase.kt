package users.application

import users.domain.IUserRepository

class DeleteUserUseCase(private val db: IUserRepository) {
    
    suspend fun execute(id: Int) {
        if (id <= 0) {
            throw IllegalArgumentException("ID inválido")
        }
        
        val existingUser = db.getById(id)
        if (existingUser == null) {
            throw IllegalArgumentException("Usuario no encontrado")
        }
        
        db.delete(id)
    }
}