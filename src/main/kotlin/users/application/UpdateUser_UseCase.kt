package users.application

import users.domain.IUserRepository
import users.domain.entities.User

class UpdateUserUseCase(private val db: IUserRepository) {
    
    suspend fun execute(user: User) {
        if (user.userId == null || user.userId <= 0) {
            throw IllegalArgumentException("ID inválido")
        }
        
        val existingUser = db.getById(user.userId)
        if (existingUser == null) {
            throw IllegalArgumentException("Usuario no encontrado")
        }
        
        db.update(user)
    }
}