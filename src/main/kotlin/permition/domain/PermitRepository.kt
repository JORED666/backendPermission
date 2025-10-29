package permition.domain

import permition.domain.entities.Permition
import permition.domain.entities.PermitStatus

interface PermitRepository {
    suspend fun save(permit: Permition): Permition
    suspend fun getById(permitId: Int): Permition?
    suspend fun getAll(): List<Permition>
    suspend fun getByStudentId(studentId: Int): List<Permition>
    suspend fun getByTutorId(tutorId: Int): List<Permition>
    suspend fun getByStatus(status: PermitStatus): List<Permition>
    suspend fun update(permit: Permition): Unit
    suspend fun delete(permitId: Int): Unit
}