package permition.domain.entities

enum class PermitReason(val displayName: String) {
    FAMILY("Family"),
    HEALTH("Health"),
    ECONOMIC("Economic"),
    ACADEMIC_EVENTS("Academic Events"),
    SPORTS("Sports"),
    PREGNANCY("Pregnancy"),
    ACCIDENTS("Accidents"),
    ADDICTIONS("Addictions"),
    PERSONAL_PROCEDURES("Personal Procedures"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): PermitReason {
            return entries.find { 
                it.displayName.equals(value, ignoreCase = true) || 
                it.name.equals(value, ignoreCase = true) 
            } ?: OTHER
        }
    }
}