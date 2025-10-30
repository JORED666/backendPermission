package teachers.domain

import teachers.domain.entities.Teacher
import teachers.domain.entities.TeacherWithDetails

interface ITeacherRepository {
    suspend fun save(teacher: Teacher): Teacher
    suspend fun getById(teacherId: Int): Teacher?
    suspend fun getAll(): List<Teacher>
    suspend fun getAllWithDetails(): List<TeacherWithDetails> 
    suspend fun getByIdWithDetails(teacherId: Int): TeacherWithDetails? 
    suspend fun getByUserId(userId: Int): Teacher?
    suspend fun update(teacher: Teacher): Unit
    suspend fun delete(teacherId: Int): Unit
}