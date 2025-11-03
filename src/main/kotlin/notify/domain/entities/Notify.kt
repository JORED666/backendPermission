package notify.domain.entities

import java.time.LocalDateTime

data class Notify(
    val notificationId: Int? = null,
    val senderId: Int,
    val receiverId: Int,
    val type: String,  // 'new_permit', 'permit_status', 'permit_assigned'
    val message: String,
    val relatedPermitId: Int?,
    val isRead: Boolean = false,
    val createdAt: LocalDateTime? = null
)