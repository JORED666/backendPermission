package core.security

import org.mindrot.jbcrypt.BCrypt

object HashService {
    private const val SALT_ROUNDS = 10
    
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(SALT_ROUNDS))
    }
    
    fun checkPassword(hashedPassword: String, password: String): Boolean {
        return try {
            BCrypt.checkpw(password, hashedPassword)
        } catch (e: Exception) {
            false
        }
    }
}