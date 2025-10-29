package permition.domain.entities

import java.time.LocalDate
import java.time.LocalDateTime

data class Permition(
    val permitId: Int? = null,
    val studentId: Int,
    val tutorId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reason: PermitReason,
    val description: String,
    val evidence: String? = null,
    val status: PermitStatus = PermitStatus.PENDING,
    val requestDate: LocalDateTime = LocalDateTime.now()
)