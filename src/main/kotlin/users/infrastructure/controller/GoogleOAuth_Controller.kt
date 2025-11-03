package users.infrastructure.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import users.application.OAuthUseCase
import core.security.OAuthService
import java.net.URLEncoder

class GoogleOAuth_Controller(
    private val oauthUseCase: OAuthUseCase,
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String,
    private val frontendUrl: String
) {
    
    suspend fun login(call: ApplicationCall) {
        println("🔵 Iniciando login con Google")
        
        val authorizeUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
            "client_id=$clientId&" +
            "redirect_uri=$redirectUri&" +
            "response_type=code&" +
            "scope=email%20profile&" +
            "access_type=offline"
        
        call.respondRedirect(authorizeUrl)
    }
    
    suspend fun callback(call: ApplicationCall) {
        try {
            val code = call.parameters["code"]
            val error = call.parameters["error"]
            
            if (error != null) {
                println("Error de Google OAuth: $error")
                call.respondRedirect("$frontendUrl/login?error=google_auth_failed")
                return
            }
            
            if (code == null) {
                println("No se recibió código de autorización")
                call.respondRedirect("$frontendUrl/login?error=no_code")
                return
            }
            
            println("Código recibido de Google")
            
            val accessToken = OAuthService.getGoogleAccessToken(code, clientId, clientSecret, redirectUri)
            println("✅ Access token obtenido")
            
            val userInfo = OAuthService.getGoogleUserInfo(accessToken)
            println("✅ Información del usuario obtenida: ${userInfo.email}")
            
            val loginData = oauthUseCase.loginOrRegisterWithOAuth(
                email = userInfo.email,
                firstName = userInfo.given_name,
                lastName = userInfo.family_name,
                oauthProvider = "google",
                oauthId = userInfo.id
            )
            
            println("✅ Usuario autenticado: ${loginData.email}")
            
            val encodedName = URLEncoder.encode(loginData.name, "UTF-8")
            val encodedEmail = URLEncoder.encode(loginData.email, "UTF-8")
            
            call.respondRedirect(
                "$frontendUrl/auth/callback?token=${loginData.token}&userId=${loginData.userId}&name=$encodedName&email=$encodedEmail"
            )
            
        } catch (error: Exception) {
            println("Error en Google OAuth callback: ${error.message}")
            error.printStackTrace()
            call.respondRedirect("$frontendUrl/login?error=auth_failed")
        }
    }
}