package permition.domain.entities

enum class PermitStatus {
    PENDING,
    APPROVED,
    REJECTED;

    companion object {
        fun fromString(value: String): PermitStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}