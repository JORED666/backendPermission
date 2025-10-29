package users.application

import users.domain.IUserRepository
import users.domain.entities.User

class GetUserByIdUseCase(private val db: IUserRepository) {
    
    suspend fun execute(id: Int): User? {
        if (id <= 0) {
            throw IllegalArgumentException("ID inválido")
        }
        
        return db.getById(id)
    }
}