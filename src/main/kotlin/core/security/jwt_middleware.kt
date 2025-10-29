package core.security

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

data class UserPrincipal(
    val userId: Int,
    val email: String
) : Principal

fun Application.configureJWTAuthentication() {
    val jwtSecret = System.getenv("JWT_SECRET") ?: "AmethToledo"
    val algorithm = Algorithm.HMAC256(jwtSecret)
    
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(algorithm)
                    .build()
            )
            
            validate { credential ->
                try {
                    val userId = credential.payload.getClaim("user_id").asInt()
                    val email = credential.payload.getClaim("email").asString()
                    
                    if (userId != null && email != null) {
                        UserPrincipal(userId, email)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token inválido o expirado")
                )
            }
        }
    }
}

val ApplicationCall.userId: Int
    get() = principal<UserPrincipal>()?.userId
        ?: throw IllegalStateException("Usuario no autenticado")

val ApplicationCall.userEmail: String
    get() = principal<UserPrincipal>()?.email
        ?: throw IllegalStateException("Usuario no autenticado")

fun Route.authenticated(build: Route.() -> Unit): Route {
    return authenticate("auth-jwt") {
        build()
    }
}