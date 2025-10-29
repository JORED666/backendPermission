package core.security

import java.security.SecureRandom

object SecurityUtils {
    
    fun generateRandomString(length: Int): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray((length + 1) / 2)
        secureRandom.nextBytes(bytes)
        
        return bytes.joinToString("") { "%02x".format(it) }.take(length)
    }
}