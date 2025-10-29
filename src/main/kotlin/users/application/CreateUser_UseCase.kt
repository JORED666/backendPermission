package users.application

import users.domain.IUserRepository
import users.domain.entities.User
import java.time.LocalDateTime

class CreateUserUseCase(private val db: IUserRepository) {
    
    suspend fun execute(user: User): User {
        if (user.firstName.isBlank()) {
            throw IllegalArgumentException("El nombre es obligatorio")
        }
        
        if (user.lastName.isBlank()) {
            throw IllegalArgumentException("El apellido es obligatorio")
        }
        
        if (user.email.isBlank()) {
            throw IllegalArgumentException("El email es obligatorio")
        }
        
        if (user.password.isBlank()) {
            throw IllegalArgumentException("La contraseña es obligatoria")
        }
        
        val existingUser = db.getByEmail(user.email)
        if (existingUser != null) {
            throw IllegalArgumentException("El email ya está registrado")
        }
        
        val userWithDate = user.copy(registrationDate = LocalDateTime.now())
        
        return db.save(userWithDate)
    }
}