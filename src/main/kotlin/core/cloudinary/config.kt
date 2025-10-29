package core.cloudinary

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

object CloudinaryConfig {
    val cloudinary: Cloudinary by lazy {
        Cloudinary(ObjectUtils.asMap(
            "cloud_name", System.getenv("CLOUD_NAME") ?: throw IllegalStateException("CLOUD_NAME no configurado"),
            "api_key", System.getenv("API_KEY") ?: throw IllegalStateException("API_KEY no configurado"),
            "api_secret", System.getenv("API_SECRET") ?: throw IllegalStateException("API_SECRET no configurado")
        ))
    }
}