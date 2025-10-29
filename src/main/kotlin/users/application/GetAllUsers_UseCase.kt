package users.application

import users.domain.IUserRepository
import users.domain.entities.User

class GetAllUsersUseCase(private val db: IUserRepository) {
    
    suspend fun execute(): List<User> {
        return db.getAll()
    }
}