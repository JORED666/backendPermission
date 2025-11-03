package core.security

import core.data.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object OAuthService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    // GOOGLE OAUTH
    suspend fun getGoogleAccessToken(
        code: String, 
        clientId: String, 
        clientSecret: String, 
        redirectUri: String
    ): String {
        val response = client.post("https://oauth2.googleapis.com/token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                "code=$code" +
                "&client_id=$clientId" +
                "&client_secret=$clientSecret" +
                "&redirect_uri=$redirectUri" +
                "&grant_type=authorization_code"
            )
        }
        
        val tokenResponse = response.body<GoogleTokenResponse>()
        return tokenResponse.access_token
    }

    suspend fun getGoogleUserInfo(accessToken: String): GoogleUserInfo {
        val response = client.get("https://www.googleapis.com/oauth2/v2/userinfo") {
            header("Authorization", "Bearer $accessToken")
        }
        return response.body<GoogleUserInfo>()
    }

    // GITHUB OAUTH
    suspend fun getGitHubAccessToken(
        code: String, 
        clientId: String, 
        clientSecret: String
    ): String {
        val response = client.post("https://github.com/login/oauth/access_token") {
            contentType(ContentType.Application.Json)
            header("Accept", "application/json")
            setBody(GitHubTokenRequest(
                client_id = clientId,
                client_secret = clientSecret,
                code = code
            ))
        }
        
        val tokenResponse = response.body<GitHubTokenResponse>()
        return tokenResponse.access_token
    }

    suspend fun getGitHubUserInfo(accessToken: String): GitHubUserInfo {
        val response = client.get("https://api.github.com/user") {
            header("Authorization", "Bearer $accessToken")
            header("Accept", "application/json")
        }
        val userInfo = response.body<GitHubUserInfo>()
        
        if (userInfo.email == null) {
            val emailResponse = client.get("https://api.github.com/user/emails") {
                header("Authorization", "Bearer $accessToken")
                header("Accept", "application/json")
            }
            val emails = emailResponse.body<List<GitHubEmail>>()
            val primaryEmail = emails.firstOrNull { it.primary && it.verified }?.email
                ?: emails.firstOrNull { it.verified }?.email
            
            return userInfo.copy(email = primaryEmail)
        }
        
        return userInfo
    }

    fun close() {
        client.close()
    }
}