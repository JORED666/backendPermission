package permition.domain

import permition.domain.entities.Permition
import permition.domain.entities.PermitStatus
import permition.domain.entities.PermitWithDetails

interface PermitRepository {
    suspend fun save(permit: Permition): Permition
    suspend fun savePermitTeachers(permitId: Int, teacherIds: List<Int>)
    suspend fun getById(permitId: Int): Permition?
    suspend fun getByIdWithDetails(permitId: Int): PermitWithDetails?
    suspend fun getAll(): List<Permition>
    suspend fun getAllWithDetails(): List<PermitWithDetails>
    suspend fun getByStudentId(studentId: Int): List<Permition>
    suspend fun getByTutorId(tutorId: Int): List<Permition>
    suspend fun getByStatus(status: PermitStatus): List<Permition>
    suspend fun update(permit: Permition): Unit
    suspend fun updatePermitTeachers(permitId: Int, teacherIds: List<Int>)
    suspend fun delete(permitId: Int): Unit
}