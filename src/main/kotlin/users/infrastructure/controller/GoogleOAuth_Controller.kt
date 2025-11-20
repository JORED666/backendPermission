package users.infrastructure.controller

import core.security.OAuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import java.net.URLEncoder
import users.application.OAuthUseCase

class GoogleOAuth_Controller(
        private val oauthUseCase: OAuthUseCase,
        private val clientId: String,
        private val clientSecret: String,
        private val redirectUri: String,
        private val frontendUrl: String
) {

    suspend fun login(call: ApplicationCall) {
        println("🔵 Iniciando login con Google")

        val authorizeUrl =
                "https://accounts.google.com/o/oauth2/v2/auth?" +
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
                call.respondRedirect("$frontendUrl?error=google_auth_failed")
                return
            }

            if (code == null) {
                println("No se recibió código de autorización")
                call.respondRedirect("$frontendUrl?error=no_code")
                return
            }

            println("Código recibido de Google")

            val accessToken =
                    OAuthService.getGoogleAccessToken(code, clientId, clientSecret, redirectUri)
            println("Access token obtenido")

            val userInfo = OAuthService.getGoogleUserInfo(accessToken)
            println("Información del usuario obtenida: ${userInfo.email}")

            val loginData =
                    oauthUseCase.loginOrRegisterWithOAuth(
                            email = userInfo.email,
                            firstName = userInfo.given_name,
                            lastName = userInfo.family_name,
                            oauthProvider = "google",
                            oauthId = userInfo.id
                    )

            println("Usuario autenticado: ${loginData.email}")

            val encodedName = URLEncoder.encode(loginData.name, "UTF-8")
            val encodedEmail = URLEncoder.encode(loginData.email, "UTF-8")

            // ✅ AGREGANDO LOS PARÁMETROS FALTANTES
            val redirectUrl = buildString {
                append("$frontendUrl/auth/callback")
                append("?token=${loginData.token}")
                append("&userId=${loginData.userId}")
                append("&roleId=${loginData.roleId}") // ← NUEVO
                append("&name=$encodedName")
                append("&email=$encodedEmail")

                // Agregar tutorId solo si existe
                loginData.tutorId?.let { append("&tutorId=$it") }

                // Agregar studentId solo si existe
                loginData.studentId?.let { append("&studentId=$it") }
            }

            call.respondRedirect(redirectUrl)
        } catch (error: IllegalArgumentException) {
            println("Error de validación: ${error.message}")
            call.respondRedirect("$frontendUrl?error=invalid_institutional_email")
        } catch (error: Exception) {
            println("Error en Google OAuth callback: ${error.message}")
            error.printStackTrace()
            call.respondRedirect("$frontendUrl?error=auth_failed")
        }
    }
}
