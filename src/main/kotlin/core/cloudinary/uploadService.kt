package core.cloudinary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class CloudinaryService {
    
    companion object {
        private fun detectResourceType(fileName: String?): String {
            val extension = fileName?.substringAfterLast(".", "")?.lowercase() ?: ""
            return when (extension) {
                "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg" -> "image"
                "mp4", "mov", "avi", "mkv", "webm" -> "video"
                "mp3", "wav", "ogg" -> "video"
                else -> "raw"
            }
        }

        private fun convertToViewUrl(url: String, fileName: String?): String {
                if (fileName?.endsWith(".pdf", ignoreCase = true) == true) {
                val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                return "https://mozilla.github.io/pdf.js/web/viewer.html?file=$encodedUrl"
            }
            return url
        }
        
        suspend fun uploadFile(
            fileBytes: ByteArray, 
            folder: String = "evidences",
            fileName: String? = null
        ): String {
            return withContext(Dispatchers.IO) {
                try {
                    val resourceType = detectResourceType(fileName)
                    
                    val uploadResult = CloudinaryConfig.cloudinary.uploader().upload(
                        fileBytes,
                        mapOf(
                            "folder" to folder,
                            "resource_type" to resourceType
                        )
                    )
                    
                    val url = uploadResult["secure_url"] as? String 
                        ?: throw IllegalStateException("No se pudo obtener la URL del archivo")
                    
                    convertToViewUrl(url, fileName)
                        
                } catch (e: Exception) {
                    throw Exception("Error al subir archivo a Cloudinary: ${e.message}", e)
                }
            }
        }
        
        suspend fun uploadFileFromFile(file: File, folder: String = "evidences"): String {
            return withContext(Dispatchers.IO) {
                try {
                    val resourceType = detectResourceType(file.name)
                    
                    val uploadResult = CloudinaryConfig.cloudinary.uploader().upload(
                        file,
                        mapOf(
                            "folder" to folder,
                            "resource_type" to resourceType
                        )
                    )
                    
                    val url = uploadResult["secure_url"] as? String 
                        ?: throw IllegalStateException("No se pudo obtener la URL del archivo")
                    
                    convertToViewUrl(url, file.name)
                        
                } catch (e: Exception) {
                    throw Exception("Error al subir archivo a Cloudinary: ${e.message}", e)
                }
            }
        }

        suspend fun uploadEvidence(fileBytes: ByteArray, fileName: String? = null): String {
            return uploadFile(fileBytes, "permits/evidences", fileName)
        }
        
        suspend fun uploadAvatar(fileBytes: ByteArray, fileName: String? = null): String {
            return uploadFile(fileBytes, "avatars", fileName)
        }
        
        suspend fun deleteFile(fileUrl: String) {
            return withContext(Dispatchers.IO) {
                try {
                    val actualUrl = if (fileUrl.contains("mozilla.github.io/pdf.js")) {
                        val urlParam = fileUrl.substringAfter("file=")
                        java.net.URLDecoder.decode(urlParam, StandardCharsets.UTF_8.toString())
                    } else {
                        fileUrl
                    }
                    
                    val publicId = extractPublicId(actualUrl)
                    val resourceType = extractResourceType(actualUrl)
                    
                    CloudinaryConfig.cloudinary.uploader().destroy(
                        publicId, 
                        mapOf("resource_type" to resourceType)
                    )
                    
                    println("Archivo eliminado de Cloudinary: $publicId")
                    
                } catch (e: Exception) {
                    println("Error al eliminar archivo de Cloudinary: ${e.message}")
                    throw Exception("Error al eliminar archivo de Cloudinary: ${e.message}", e)
                }
            }
        }
        
        private fun extractResourceType(imageUrl: String): String {
            return when {
                imageUrl.contains("/image/upload/") -> "image"
                imageUrl.contains("/video/upload/") -> "video"
                imageUrl.contains("/raw/upload/") -> "raw"
                else -> "image"
            }
        }
        
        private fun extractPublicId(imageUrl: String): String {
            val cleanUrl = imageUrl.split("/upload/").lastOrNull() ?: return ""
            val parts = cleanUrl.split("/")
            
            val pathStart = parts.indexOfFirst { it.startsWith("v") && it.length > 1 } + 1
            if (pathStart <= 0 || pathStart >= parts.size) return ""
            
            val pathParts = parts.subList(pathStart, parts.size)
            val fileNameWithExtension = pathParts.last()
            val fileName = fileNameWithExtension.substringBeforeLast(".")
            val folder = pathParts.dropLast(1).joinToString("/")
            
            return if (folder.isNotEmpty()) "$folder/$fileName" else fileName
        }
    }
}