package core.cloudinary

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import io.github.cdimascio.dotenv.dotenv

object CloudinaryConfig {
    private val dotenv = dotenv {
        directory = "./"
        filename = ".env"
        ignoreIfMissing = true
    }

    val cloudinary: Cloudinary by lazy {
        Cloudinary(ObjectUtils.asMap(
            "cloud_name", dotenv["CLOUD_NAME"] ?: throw IllegalStateException("CLOUD_NAME no configurado"),
            "api_key", dotenv["API_KEY"] ?: throw IllegalStateException("API_KEY no configurado"),
            "api_secret", dotenv["API_SECRET"] ?: throw IllegalStateException("API_SECRET no configurado")
        ))
    }
}