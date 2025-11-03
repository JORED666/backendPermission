package users.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import users.application.OAuthUseCase
import core.security.OAuthService
import java.net.URLEncoder

class GitHubOAuth_Controller(
    private val oauthUseCase: OAuthUseCase,
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String,
    private val frontendUrl: String
) {
    
    suspend fun login(call: ApplicationCall) {
        println("⚫ Iniciando login con GitHub")
        
        val authorizeUrl = "https://github.com/login/oauth/authorize?" +
            "client_id=$clientId&" +
            "redirect_uri=$redirectUri&" +
            "scope=user:email"
        
        call.respondRedirect(authorizeUrl)
    }
    
    suspend fun callback(call: ApplicationCall) {
        try {
            val code = call.parameters["code"]
            val error = call.parameters["error"]
            
            if (error != null) {
                println("Error de GitHub OAuth: $error")
                call.respondRedirect("$frontendUrl/login?error=github_auth_failed")
                return
            }
            
            if (code == null) {
                println("No se recibió código de autorización")
                call.respondRedirect("$frontendUrl/login?error=no_code")
                return
            }
            
            println("Código recibido de GitHub")
            
            val accessToken = OAuthService.getGitHubAccessToken(code, clientId, clientSecret)
            println("Access token obtenido")
            
            val userInfo = OAuthService.getGitHubUserInfo(accessToken)
            println("Información del usuario obtenida: ${userInfo.email ?: userInfo.login}")
            
            if (userInfo.email == null) {
                println("No se pudo obtener el email de GitHub")
                call.respondRedirect("$frontendUrl/login?error=no_email")
                return
            }
            
            val fullName = userInfo.name ?: userInfo.login
            val nameParts = fullName.split(" ", limit = 2)
            val firstName = nameParts.firstOrNull() ?: userInfo.login
            val lastName = if (nameParts.size > 1) nameParts[1] else "GitHub"
            
            val loginData = oauthUseCase.loginOrRegisterWithOAuth(
                email = userInfo.email,
                firstName = firstName,
                lastName = lastName,
                oauthProvider = "github",
                oauthId = userInfo.id.toString()
            )
            
            println("Usuario autenticado: ${loginData.email}")
            
            val encodedName = URLEncoder.encode(loginData.name, "UTF-8")
            val encodedEmail = URLEncoder.encode(loginData.email, "UTF-8")
            
            call.respondRedirect(
                "$frontendUrl/auth/callback?token=${loginData.token}&userId=${loginData.userId}&name=$encodedName&email=$encodedEmail"
            )
            
        } catch (error: Exception) {
            println("Error en GitHub OAuth callback: ${error.message}")
            error.printStackTrace()
            call.respondRedirect("$frontendUrl/login?error=auth_failed")
        }
    }
}