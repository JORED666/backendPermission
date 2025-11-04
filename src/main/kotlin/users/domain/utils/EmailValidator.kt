package users.domain.utils

data class RoleInfo(
    val isValid: Boolean,
    val roleId: Int,
    val isTeacher: Boolean,
    val errorMessage: String? = null
)

object EmailValidator {
    private const val INSTITUTIONAL_DOMAIN = "@ids.upchiapas.edu.mx"
    
    fun validateAndGetRole(email: String): RoleInfo {
        if (!email.endsWith(INSTITUTIONAL_DOMAIN)) {
            return RoleInfo(
                isValid = false,
                roleId = 0,
                isTeacher = false,
                errorMessage = "Debes usar una cuenta institucional ($INSTITUTIONAL_DOMAIN)"
            )
        }
        
        val localPart = email.substringBefore("@")
        
        if (localPart.isEmpty()) {
            return RoleInfo(
                isValid = false,
                roleId = 0,
                isTeacher = false,
                errorMessage = "Correo electrónico inválido"
            )
        }
        
        val firstChar = localPart.first()
        
        return when {
            firstChar.isDigit() -> {
                RoleInfo(
                    isValid = true,
                    roleId = 3,
                    isTeacher = false
                )
            }
            firstChar.isLetter() -> {
                RoleInfo(
                    isValid = true,
                    roleId = 1,
                    isTeacher = true
                )
            }
            else -> {
                RoleInfo(
                    isValid = false,
                    roleId = 0,
                    isTeacher = false,
                    errorMessage = "El correo debe empezar con una letra o número"
                )
            }
        }
    }
}