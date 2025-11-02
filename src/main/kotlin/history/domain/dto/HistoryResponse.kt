package history.domain.dto

import kotlinx.serialization.Serializable
import history.domain.entities.History

@Serializable
data class CreateHistoryRequest(
    val permitId: Int,
    val studentId: Int,
    val fechaInicio: String?,  
    val fechaFin: String?,    
    val motivo: String,  
    val estado: String = "pending" 
)

@Serializable
data class UpdateHistoryStatusRequest(
    val estado: String  
)

@Serializable
data class PermitInfoResponse(
    val permitId: Int,
    val descripcion: String?,
    val evidencia: String?,
    val cuatrimestre: Int
)

@Serializable
data class StudentInfoResponse(
    val studentId: Int,
    val matricula: String,
    val nombreCompleto: String,
    val email: String
)

@Serializable
data class HistoryWithDetailsResponse(
    val historyId: Int,
    val permitId: Int,
    val studentId: Int,
    val fechaInicio: String?,
    val fechaFin: String?,
    val motivo: String,
    val estado: String,
    val fechaSolicitud: String,
    val informacionPermiso: PermitInfoResponse,
    val informacionEstudiante: StudentInfoResponse
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): HistoryWithDetailsResponse {
            return HistoryWithDetailsResponse(
                historyId = data["history_id"] as Int,
                permitId = data["permit_id"] as Int,
                studentId = data["student_id"] as Int,
                fechaInicio = data["start_date"]?.toString(),
                fechaFin = data["end_date"]?.toString(),
                motivo = data["reason"] as String,
                estado = data["status"] as String,
                fechaSolicitud = data["request_date"].toString(),
                informacionPermiso = PermitInfoResponse(
                    permitId = data["permit_id"] as Int,
                    descripcion = data["permit_description"] as? String,
                    evidencia = data["permit_evidence"] as? String,
                    cuatrimestre = data["permit_cuatrimestre"] as Int
                ),
                informacionEstudiante = StudentInfoResponse(
                    studentId = data["student_id"] as Int,
                    matricula = data["enrollment_number"] as String,
                    nombreCompleto = (data["student_name"] as String).trim(),
                    email = data["student_email"] as String
                )
            )
        }
    }
}

@Serializable
data class HistoryResponse(
    val historyId: Int?,
    val permitId: Int,
    val studentId: Int,
    val fechaInicio: String?,
    val fechaFin: String?,
    val motivo: String,
    val estado: String,
    val fechaSolicitud: String?
) {
    companion object {
        fun fromHistory(history: History): HistoryResponse {
            return HistoryResponse(
                historyId = history.historyId,
                permitId = history.permitId,
                studentId = history.studentId,
                fechaInicio = history.startDate?.toString(),
                fechaFin = history.endDate?.toString(),
                motivo = history.reason,
                estado = history.status,
                fechaSolicitud = history.requestDate?.toString()
            )
        }
    }
}

@Serializable
data class HistoryListResponse(
    val histories: List<HistoryWithDetailsResponse>,
    val total: Int
)

@Serializable
data class SingleHistoryResponse(
    val history: HistoryWithDetailsResponse
)

@Serializable
data class CreateHistoryResponse(
    val message: String,
    val history: HistoryResponse
)

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)