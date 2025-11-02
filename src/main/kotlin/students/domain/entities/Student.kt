package students.domain.entities

data class Student(
    val studentId: Int? = null,
    val userId: Int,
    val enrollmentNumber: String,
    val familyTutorPhone: String?,
    val tutorId: Int? = null
)