package com.example.permition.domain.entitie

import  kotlinx.serialization.Serializable

@Serializable

data class  Permit(
    val permitId: Int? = null,
    val studentId: Int,
    val tutorId: Int,
    val startDate: String,
    val endDate: String,
    val reason: PermitReason,
    val description: String?,
    val evidence: String?,
    val status: PermitStatus = PermitStatus.PENDING,
    val requestDate: String? = null,

    val studentName: String? = null,
    val studentEnrollment: String? = null,
    val tutorName: String? = null,

    val teachers: List<PermitTeacher>? = null
)

@Serializabele

data class PermitTeacher(
    val teacherId: Int,
    val teacherName: String
)

@Serializable
enum class PermitReason{
    FAMILY,
    HEALTH,
    ECONOMIC,
    ACADEMIC_EVENTS,
    SPORTS,
    PREGNANCY,
    ACCIDENTS,
    ADDICTIONS,
    PERSONAL_PROCEDURES,
    OTHER;
}

companion object{
    fun  fromString(value: String): PermitReason{
        return when (value.uppercase()){
            "FAMILY" -> FAMILY
            "HEALTH" -> HEALTH
            "ECONOMIC" -> ECONOMIC
            "ACADEMIC EVENTS", "ACADEMIC_EVENTS" -> ACADEMIC_EVENTS
            "SPORTS" -> SPORTS
            "PREGNANCY" -> PREGNANCY
            "ACCIDENTS" -> ACCIDENTS
            "ADDICTIONS" -> ADDICTIONS
            "PERSONAL PROCEDURES", "PERSONAL_PROCEDURES" -> PERSONAL_ PROCEDURES
            else -> OTHER
        }
    }
    fun toDBString(reason: PermitReason): String{
        return when (reasin){
            ACADEMIC_EVENTS -> "Academic Events"
            PERSONAL_PROCEDURES -> "Personal Procedures"
            else -> reason.name. Lovercase().replaceFirstChar { it.uppercase)
        }
    }

}
    @Serializable
    enum class PermitStatus{
        PENDING,
        APPROVED,
        REJECTED;

        companion object{
            fun fromString(value: String): PermitStatus {
                return when (value.lowercase()){
                    "pending" -> PENDING
                    "approved" -> APPROVED
                    "rejected" -> REJECTED
                    else -> PENDING
                }
            }
        }

    }
}

@Serializable
data class CreatePermitRequest(
    val studentId: Int,
    val tutorId: Int,
    val startDate: String,
    val endDate: String,
    val reason: PermitReason,
    val description: String?,
    val evidence: String?,
    val teacherIds: List<Int> = emptyList()
)

@Serializable
data class UpdatePermitRequest(
    val startDate: String?,
    val endDate: String?,
    val reason: PermitReason?,
    val description: String?,
    val evidence: String?,
    val status: PernitStatus?,
    val teacherIds: List<Int>?
)

@Serializable
data class  UpdatePermitStatusRequest(
    val status: PermitStatus
)