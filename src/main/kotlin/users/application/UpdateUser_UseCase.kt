package users.application

import users.domain.IUserRepository
import users.domain.dto.UpdateUserRequest

class UpdateUserUseCase(private val db: IUserRepository) {
    
    suspend fun execute(userId: Int, updateData: UpdateUserRequest) {
        if (userId <= 0) {
            throw IllegalArgumentException("ID inválido")
        }
        
        val existingUser = db.getById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
          val updatedUser = existingUser.copy(
            firstName = updateData.firstName ?: existingUser.firstName,
            middleName = updateData.middleName ?: existingUser.middleName,
            lastName = updateData.lastName ?: existingUser.lastName,
            secondLastName = updateData.secondLastName ?: existingUser.secondLastName,
            phone = updateData.phone ?: existingUser.phone
         )
        
        db.update(updatedUser)
    }
}
