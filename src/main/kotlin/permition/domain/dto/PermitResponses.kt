package permition.domain.dto

import kotlinx.serialization.Serializable
import permition.domain.entities.Permition
import permition.domain.entities.PermitWithDetails

@Serializable
data class StudentInfoResponse(
    val studentId: Int,
    val userId: Int,
    val nombreCompleto: String,
    val email: String,
    val telefono: String?,
    val numeroMatricula: String?
)

@Serializable
data class TutorInfoResponse(
    val tutorId: Int,
    val userId: Int,
    val nombreCompleto: String,
    val email: String,
    val telefono: String?
)

@Serializable
data class TeacherInfoResponse(
    val teacherId: Int,
    val userId: Int,
    val nombreCompleto: String,
    val email: String,
    val telefono: String?
)

@Serializable
data class PermitWithDetailsResponse(
    val permitId: Int,
    val estudiante: StudentInfoResponse,
    val tutor: TutorInfoResponse,
    val profesores: List<TeacherInfoResponse>,
    val startDate: String,
    val endDate: String,
    val reason: String,
    val description: String,
    val cuatrimestre: Int, 
    val evidence: String?,
    val status: String,
    val requestDate: String,
    val permitDocumentUrl: String? = null
) {
    companion object {
        fun fromPermitWithDetails(permit: PermitWithDetails): PermitWithDetailsResponse {
            return PermitWithDetailsResponse(
                permitId = permit.permitId,
                estudiante = StudentInfoResponse(
                    studentId = permit.studentInfo.studentId,
                    userId = permit.studentInfo.userId,
                    nombreCompleto = permit.studentInfo.fullName,
                    email = permit.studentInfo.email,
                    telefono = permit.studentInfo.phone,
                    numeroMatricula = permit.studentInfo.enrollmentNumber
                ),
                tutor = TutorInfoResponse(
                    tutorId = permit.tutorInfo.tutorId,
                    userId = permit.tutorInfo.userId,
                    nombreCompleto = permit.tutorInfo.fullName,
                    email = permit.tutorInfo.email,
                    telefono = permit.tutorInfo.phone
                ),
                profesores = permit.teachers.map { teacher ->
                    TeacherInfoResponse(
                        teacherId = teacher.teacherId, 
                        userId = teacher.userId, 
                        nombreCompleto = teacher.fullName, 
                        email = teacher.email, 
                        telefono = teacher.phone
                    )
                },
                startDate = permit.startDate.toString(),
                endDate = permit.endDate.toString(),
                reason = permit.reason.displayName,
                description = permit.description,
                cuatrimestre = permit.cuatrimestre, 
                evidence = permit.evidence,
                status = permit.status.name.lowercase(),
                requestDate = permit.requestDate.toString(),
                permitDocumentUrl = permit.permitDocumentUrl
            )
        }
    }
}

@Serializable
data class PermitResponse(
    val permitId: Int?,
    val studentId: Int,
    val tutorId: Int,
    val teacherIds: List<Int>,
    val startDate: String,
    val endDate: String,
    val reason: String,
    val description: String,
    val cuatrimestre: Int, 
    val evidence: String?,
    val status: String,
    val requestDate: String,
    val permitDocumentUrl: String? = null
) {
    companion object {
        fun fromPermit(permit: Permition): PermitResponse {
            return PermitResponse(
                permitId = permit.permitId,
                studentId = permit.studentId,
                tutorId = permit.tutorId,
                teacherIds = permit.teacherIds,
                startDate = permit.startDate.toString(),
                endDate = permit.endDate.toString(),
                reason = permit.reason.displayName,
                description = permit.description,
                cuatrimestre = permit.cuatrimestre,  
                evidence = permit.evidence,
                status = permit.status.name.lowercase(),
                requestDate = permit.requestDate.toString(),
                permitDocumentUrl = permit.permitDocumentUrl 
            )
        }
    }
}

@Serializable
data class PermitListResponse(
    val permits: List<PermitWithDetailsResponse>,
    val total: Int
)

@Serializable
data class SinglePermitResponse(
    val permit: PermitWithDetailsResponse
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

@Serializable
data class CreatePermitWithDetailsResponse(
    val message: String,
    val permit: PermitWithDetailsResponse
)