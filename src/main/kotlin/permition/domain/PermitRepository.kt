package com. example.permition.domain

import com.example.permition.domain.entitie.CreatePermitRequest
import com.example.permition.domain.entitie.Permit
import com.example.permition.domain.entitie.PermitStatus
import com.example.permition.domain.entitie.UpdatePermitRequest

interface PermitRepository{
    suspend fun createPermit(request: CreatePermitRequest): Permit?
    suspend fun getAllPermits(): List<Permit>
    suspend fun getPermitById(permitId: Int): Permit?
    suspend fun getPermitsByStudentId(studentId: Int): List<Permit>
    suspend fun getPermitsByTutorId(tutorId: Int): List<Permit>
    suspend fun getPermitsByStatus(status: PermitStatus): List<Permit>
    suspend fun updatePermit(permitId: Int, request: UpdatePermitRequest): Boolean
    suspend fun updatePermitStatus(permitId: Int, status: PermitStatus): Boolean
    suspend fun deletePermit(permitId: Int): Boolean
    suspend fun assignTeachersToPermit(permitId: Int, teacherIds: List<Int>): Boolean
    suspend fun removeTeachersFromPermit(permitId: Int): Boolean
    suspend fun getTeachersByPermitId(permitId: Int): List<Int>
}