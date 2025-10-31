package permition.domain.entities

import java.time.LocalDate
import java.time.LocalDateTime

data class Permition(
    val permitId: Int? = null,
    val studentId: Int,
    val tutorId: Int,
    val teacherIds: List<Int> = emptyList(),
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reason: PermitReason,
    val description: String,
    val cuatrimestre: Int,
    val evidence: String? = null,
    val status: PermitStatus = PermitStatus.PENDING,
    val requestDate: LocalDateTime = LocalDateTime.now()
)
