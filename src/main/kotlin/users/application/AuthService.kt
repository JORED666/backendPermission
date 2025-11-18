package users.application

import users.domain.IUserRepository
import users.domain.entities.User
import users.domain.dto.LoginResponse  
import tutors.domain.ITutorRepository
import students.domain.IStudentRepository  
import core.security.HashService
import core.security.AuthService 
import java.time.LocalDateTime

class AuthServiceUseCase(
    private val userRepo: IUserRepository,
    private val tutorRepo: ITutorRepository,
    private val studentRepo: IStudentRepository  
) {
    
    suspend fun login(email: String, password: String): LoginResponse {
        val trimmedEmail = email.trim()
        println("Buscando usuario con correo: $trimmedEmail")
        
        val user = userRepo.getByEmail(trimmedEmail)
        
        if (user == null) {
            println("Usuario no encontrado (null)")
            throw IllegalArgumentException("Usuario no encontrado")
        }
        
        val isValidPassword = HashService.checkPassword(user.password, password)
        
        if (!isValidPassword) {
            println("Contraseña incorrecta")
            throw IllegalArgumentException("Contraseña incorrecta")
        }
        
        val tutor = tutorRepo.getByUserId(user.userId!!)
        val tutorId = tutor?.tutorId
        
        val student = studentRepo.getByUserId(user.userId)
        val studentId = student?.studentId
        
        println("Usuario ${user.userId} - RoleId: ${user.roleId} - TutorId: $tutorId - StudentId: $studentId")
        
        val token = AuthService.generateJWT(user.userId, user.email)
        
        return LoginResponse(
            token = token,
            userId = user.userId,
            tutorId = tutorId,
            studentId = studentId,  
            roleId = user.roleId,   
            name = user.firstName,
            email = user.email
        )
    }
    
    suspend fun register(user: User): User {
        val existingUser = userRepo.getByEmail(user.email)
        if (existingUser != null) {
            throw IllegalArgumentException("El email ya está registrado")
        }
        
        val hashedPassword = HashService.hashPassword(user.password)
        
        val userToSave = user.copy(
            password = hashedPassword,
            registrationDate = LocalDateTime.now()
        )
        
        return userRepo.save(userToSave)
    }
}