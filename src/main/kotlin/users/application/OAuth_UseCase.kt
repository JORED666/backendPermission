package users.application

import users.domain.IUserRepository
import users.domain.entities.User
import users.domain.dto.LoginResponse
import users.domain.utils.EmailValidator 
import core.security.AuthService
import java.time.LocalDateTime

class OAuthUseCase(private val userRepo: IUserRepository) {
    
    suspend fun loginOrRegisterWithOAuth(
        email: String,
        firstName: String,
        lastName: String,
        oauthProvider: String,
        oauthId: String,
        middleName: String? = null
    ): LoginResponse {
        
        val roleInfo = EmailValidator.validateAndGetRole(email.trim())
        
        if (!roleInfo.isValid) {
            throw IllegalArgumentException(
                roleInfo.errorMessage ?: "Debes usar una cuenta institucional (@ids.upchiapas.edu.mx)"
            )
        }
        
        println("Buscando usuario OAuth: provider=$oauthProvider, oauthId=$oauthId")
        
        var user = userRepo.getByOAuthId(oauthProvider, oauthId)
        
        if (user != null) {
            println("Usuario OAuth encontrado: ${user.email}")
        } else {
            println("Usuario OAuth no encontrado, buscando por email...")
            
            user = userRepo.getByEmail(email.trim())
            
            if (user != null) {
                println("⚠️ Usuario encontrado por email: ${user.email}")
                println("El usuario ya existe con registro tradicional")
            } else {
                println("Usuario no existe, creando nuevo...")
                
                val newUser = User(
                    firstName = firstName,
                    middleName = middleName,
                    lastName = lastName,
                    secondLastName = null,
                    email = email.trim(),
                    phone = null,
                    password = "", 
                    registrationDate = LocalDateTime.now(),
                    roleId = roleInfo.roleId, 
                    oauthProvider = oauthProvider,
                    oauthId = oauthId
                )
                
                user = userRepo.save(newUser)
                println("Usuario OAuth creado: ${user.email} con ID: ${user.userId}, rol: ${roleInfo.roleId}")
                
                val savedUserId = user.userId
                if (roleInfo.isTeacher && savedUserId != null) {
                    userRepo.insertTeacher(savedUserId)
                    println("Usuario ${user.email} también registrado como Teacher")
                }
            }
        }
        
        val userId = user.userId ?: throw IllegalStateException("Usuario sin ID")
        
        val token = AuthService.generateJWT(userId, user.email)
        println("🔑 Token JWT generado para: ${user.email}")
        
        return LoginResponse(
            token = token,
            userId = userId, 
            name = user.firstName,
            email = user.email
        )
    }
}