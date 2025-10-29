package permition.domain.dto

import kotlinx.serialization.Serializable
import permition.domain.entities.Permition

@Serializable
data class PermitResponse(
    val permitId: Int?,
    val studentId: Int,
    val tutorId: Int,
    val startDate: String,
    val endDate: String,
    val reason: String,
    val description: String,
    val evidence: String?,
    val status: String,
    val requestDate: String
) {
    companion object {
        fun fromPermit(permit: Permition): PermitResponse {
            return PermitResponse(
                permitId = permit.permitId,
                studentId = permit.studentId,
                tutorId = permit.tutorId,
                startDate = permit.startDate.toString(),
                endDate = permit.endDate.toString(),
                reason = permit.reason.displayName,
                description = permit.description,
                evidence = permit.evidence,
                status = permit.status.name.lowercase(),
                requestDate = permit.requestDate.toString()
            )
        }
    }
}

@Serializable
data class PermitListResponse(
    val permits: List<PermitResponse>,
    val total: Int
)

@Serializable
data class SinglePermitResponse(
    val permit: PermitResponse
)

@Serializable
data class CreatePermitResponse(
    val message: String,
    val permit: PermitResponse
)

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)