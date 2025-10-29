package core.cloudinary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CloudinaryService {
    
    companion object {
        /**
         * Sube un archivo a Cloudinary desde un Buffer/ByteArray
         * @param fileBytes Los bytes del archivo
         * @param folder La carpeta en Cloudinary (default: "evidences")
         * @return La URL segura del archivo subido
         */
        suspend fun uploadFile(fileBytes: ByteArray, folder: String = "evidences"): String {
            return withContext(Dispatchers.IO) {
                try {
                    val uploadResult = CloudinaryConfig.cloudinary.uploader().upload(
                        fileBytes,
                        mapOf(
                            "folder" to folder,
                            "resource_type" to "auto"
                        )
                    )
                    
                    uploadResult["secure_url"] as? String 
                        ?: throw IllegalStateException("No se pudo obtener la URL del archivo")
                        
                } catch (e: Exception) {
                    throw Exception("Error al subir archivo a Cloudinary: ${e.message}", e)
                }
            }
        }
        
        /**
         * Sube un archivo a Cloudinary desde un File
         * @param file El archivo a subir
         * @param folder La carpeta en Cloudinary (default: "evidences")
         * @return La URL segura del archivo subido
         */
        suspend fun uploadFileFromFile(file: File, folder: String = "evidences"): String {
            return withContext(Dispatchers.IO) {
                try {
                    val uploadResult = CloudinaryConfig.cloudinary.uploader().upload(
                        file,
                        mapOf(
                            "folder" to folder,
                            "resource_type" to "auto"
                        )
                    )
                    
                    uploadResult["secure_url"] as? String 
                        ?: throw IllegalStateException("No se pudo obtener la URL del archivo")
                        
                } catch (e: Exception) {
                    throw Exception("Error al subir archivo a Cloudinary: ${e.message}", e)
                }
            }
        }
        
        /**
         * Métodos específicos para evidencias
         */
        suspend fun uploadEvidence(fileBytes: ByteArray): String {
            return uploadFile(fileBytes, "evidences")
        }
        
        /**
         * Método específico para avatares de usuarios
         */
        suspend fun uploadAvatar(fileBytes: ByteArray): String {
            return uploadFile(fileBytes, "avatars")
        }
        
        /**
         * Elimina un archivo de Cloudinary usando su URL
         * @param fileUrl La URL completa del archivo
         */
        suspend fun deleteFile(fileUrl: String) {
            return withContext(Dispatchers.IO) {
                try {
                    val publicId = extractPublicId(fileUrl)
                    
                    CloudinaryConfig.cloudinary.uploader().destroy(publicId, emptyMap<String, Any>())
                    
                    println("Archivo eliminado de Cloudinary: $publicId")
                    
                } catch (e: Exception) {
                    println("Error al eliminar archivo de Cloudinary: ${e.message}")
                    throw Exception("Error al eliminar archivo de Cloudinary: ${e.message}", e)
                }
            }
        }
        
        /**
         * Extrae el public_id de una URL de Cloudinary
         * Ejemplo: https://res.cloudinary.com/demo/image/upload/v1234/evidences/image.jpg
         * Retorna: evidences/image
         */
        private fun extractPublicId(imageUrl: String): String {
            val parts = imageUrl.split("/")
            val fileNameWithExtension = parts.last()
            val fileName = fileNameWithExtension.substringBeforeLast(".")
            val folder = parts[parts.size - 2]
            
            return "$folder/$fileName"
        }
    }
}