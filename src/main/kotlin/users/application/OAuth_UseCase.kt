package users.application

import users.domain.IUserRepository
import users.domain.entities.User
import users.domain.dto.LoginResponse
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
        
        println("🔍 Buscando usuario OAuth: provider=$oauthProvider, oauthId=$oauthId")
        
        var user = userRepo.getByOAuthId(oauthProvider, oauthId)
        
        if (user != null) {
            println("Usuario OAuth encontrado: ${user.email}")
        } else {
            println("Usuario OAuth no encontrado, buscando por email...")
            
            user = userRepo.getByEmail(email.trim())
            
            if (user != null) {
                println("Usuario encontrado por email: ${user.email}")
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
                    roleId = 3, 
                    oauthProvider = oauthProvider,
                    oauthId = oauthId
                )
                
                user = userRepo.save(newUser)
                println("Usuario OAuth creado: ${user.email} con ID: ${user.userId}")
            }
        }
        
        val userId = user.userId ?: throw IllegalStateException("Usuario sin ID")
        
        val token = AuthService.generateJWT(userId, user.email)
        println("Token JWT generado para: ${user.email}")
        
        return LoginResponse(
            token = token,
            userId = userId, 
            name = user.firstName,
            email = user.email
        )
    }
}