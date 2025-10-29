package com.example.permition.application

import com.example.permition.domain.PermitRepository
import com example.permition.domain.dto.PermitResponse

class DeletePermit_UseCase(private val  repository: PermitRepository){

    suspend operator fun invoke(permitId: Int): PermitResponse{
        //Verificar que el permiso existe
        val existingPermit = repository.getPermitById(permitId)
        if(existingPermit == null){
            return PermitResponse(
                success = false,
                message = "Permiso no encontrado"
            )
        }
        val deleted = repository.deletePermit(permitId)

        return if (deleted){
            PermitResponse(
                success = true,
                message = "Permiso eliminado exitosamente"
            )
        }else{
            PermitResponse(
                success = false,
                message = "Error al eliminar el permiso"
            )
        }
    }
}