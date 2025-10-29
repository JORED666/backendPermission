package users.domain.entitie

import student.domain.entitie.student

interface StudentRepository{
    suspend fun createStudent(request: CreateStudentRequest):Student?
    suspend fun getAllStudents():List<Student>
    suspend fun getStudentById(studentId: Int): Student?
    suspend fun getStudentByUserId(userId: Int): Student?
    suspend fun updateStudent(studentId: Int, request: UpdateStudentRequest): Boolean
    suspend fun deleteStudent(studentId: Int): Boolean
    suspend fun studentExistByEnrollment(enrollmentNumber: String): Boolean
}