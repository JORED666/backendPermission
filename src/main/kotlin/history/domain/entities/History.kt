package history.domain.entities

import java.time.LocalDate
import java.time.LocalDateTime

data class History(
    val historyId: Int? = null,
    val permitId: Int,
    val studentId: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val reason: String,
    val status: String,
    val requestDate: LocalDateTime? = null
)