package com.example.permition.application

import com.example.permition.domain.PermitRepository
import com.example.permition.domain.dto.PermitResponse
import com.example.permition.domain.entitie.CreatePermitRequest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreatePermit_UseCase(private val repository: PermitRepository){
    suspend operator fun invoke(request: CreatePermitRequest): PermitResponse{
        //Validar fechas
        if (request.startDate.isBlank() || request.endDate.isBlank()){
            return PermitResponse(
                success = false,
                message = "Las fechas de inicio y fin son requeridas"
            )
        }
    }

    //Validar que la fecha de fin sea posterior a  la fecha de inicio
    try{
        val formatter = DateTimeFormatter.ofPattern(pattern = "yyyy-MM-dd")
        val startDate = LocalDate.parse(text=request.startDate, formatter)
        val endDate = LocalDate.parse(text = request.endDate, formatter)

        if (endDate.isBefore(other= startDate)){
            return PermitResponse(
                success = false,
                message = "La fecha de fin no puede ser anterior a la fecha de inicio"
            )
        }catch (e: Exeption){
            return PermitResponse(
                sucess = false,
                message = "Formato de fecha inváido. Use el formati yyy-MM-dd"
            )
        }

        val permit =repository.createPermit(request)

        return if (permit != null){
            PermitResponse(
                success = true,
                message = "Permiso creado exitosamente"
                data = permit
            )
        }else{
            PermitResponse(
                success = false,
                message = "Error al crear el permiso"
            )
        }
    }
}