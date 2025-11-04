package users.domain

import users.domain.entities.User

interface IUserRepository {
    suspend fun save(user: User): User
    suspend fun getByEmail(email: String): User?
    suspend fun getByOAuthId(provider: String, oauthId: String): User? 
    suspend fun getAll(): List<User>
    suspend fun getById(id: Int): User?
    suspend fun update(user: User): Unit
    suspend fun delete(id: Int): Unit
    suspend fun insertTeacher(userId: Int)
}