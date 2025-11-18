package permition.domain.entities

import java.time.LocalDate
import java.time.LocalDateTime

data class PermitWithDetails(
    val permitId: Int,
    val studentInfo: StudentInfo,
    val tutorInfo: TutorInfo,
    val teachers: List<TeacherInfo>,     
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reason: PermitReason,
    val description: String,
    val cuatrimestre: Int,
    val evidence: String?,
    val status: PermitStatus,
    val requestDate: LocalDateTime,
    val permitDocumentUrl: String? = null
)

data class StudentInfo(
    val studentId: Int,
    val userId: Int,
    val fullName: String,
    val email: String,
    val phone: String?,
    val enrollmentNumber: String?
)

data class TutorInfo(
    val tutorId: Int,
    val userId: Int,
    val fullName: String,
    val email: String,
    val phone: String?
)

data class TeacherInfo(
    val teacherId: Int,
    val userId: Int,
    val fullName: String,
    val email: String,
    val phone: String?
)