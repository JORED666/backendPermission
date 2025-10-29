package core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

data class Claims(
    val userId: Int,
    val email: String
)

object AuthService {
    private val JWT_SECRET = System.getenv("JWT_SECRET") ?: "AmethToledo"
    private const val EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 horas en milisegundos
    
    private val algorithm = Algorithm.HMAC256(JWT_SECRET)
    
    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        .build()
    
    fun generateJWT(userId: Int, email: String): String {
        val expirationTime = Date(System.currentTimeMillis() + EXPIRATION_TIME)
        
        return JWT.create()
            .withClaim("user_id", userId)
            .withClaim("email", email)
            .withExpiresAt(expirationTime)
            .sign(algorithm)
    }
    
    fun validateJWT(tokenString: String): Claims? {
        return try {
            val decoded = verifier.verify(tokenString)
            Claims(
                userId = decoded.getClaim("user_id").asInt(),
                email = decoded.getClaim("email").asString()
            )
        } catch (e: Exception) {
            null
        }
    }
}