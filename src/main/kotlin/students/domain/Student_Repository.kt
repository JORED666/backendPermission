package students.domain

import students.domain.entities.Student

interface IStudentRepository {
    suspend fun save(student: Student): Student
    suspend fun getById(studentId: Int): Student?
    suspend fun getAll(): List<Student>
    suspend fun getAllWithDetails(): List<Map<String, Any?>>
    suspend fun getByIdWithDetails(studentId: Int): Map<String, Any?>?
    suspend fun getByUserId(userId: Int): Student?
    suspend fun getByEnrollmentNumber(enrollmentNumber: String): Student?
    suspend fun search(query: String): List<Map<String, Any?>>
    suspend fun update(student: Student): Unit
    suspend fun delete(studentId: Int): Unit
    suspend fun getTutorIdByStudentId(studentId: Int): Int?
    suspend fun getStudentsByTutorId(tutorId: Int): List<Map<String, Any?>>
}