package permitsTeacher.domain

import permitsTeacher.domain.entities.PermitTeacher

interface IPermitTeacherRepository {
    suspend fun save(permitTeacher: PermitTeacher): PermitTeacher
    suspend fun getByPermitAndTeacher(permitId: Int, teacherId: Int): PermitTeacher?
    suspend fun getAll(): List<PermitTeacher>
    suspend fun getAllWithDetails(): List<Map<String, Any?>>
    suspend fun getByPermitId(permitId: Int): List<PermitTeacher>
    suspend fun getByTeacherId(teacherId: Int): List<PermitTeacher>
    suspend fun getByPermitIdWithDetails(permitId: Int): List<Map<String, Any?>>
    suspend fun getByTeacherIdWithDetails(teacherId: Int): List<Map<String, Any?>>
    suspend fun delete(permitId: Int, teacherId: Int): Unit
    suspend fun deleteByPermitId(permitId: Int): Unit
    suspend fun deleteByTeacherId(teacherId: Int): Unit
}