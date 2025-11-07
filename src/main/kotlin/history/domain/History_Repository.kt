package history.domain

import history.domain.entities.History
import history.domain.dto.HistoryResponse

interface IHistoryRepository {
    suspend fun save(history: History): History
    suspend fun getById(historyId: Int): History?
    suspend fun getAll(): List<History>
    suspend fun getAllWithDetails(): List<Map<String, Any?>>
    suspend fun getByIdWithDetails(historyId: Int): Map<String, Any?>?
    suspend fun getByStudentId(studentId: Int): List<History>
    suspend fun getByStudentIdWithDetails(studentId: Int): List<Map<String, Any?>>
    suspend fun getByTutorId(tutorId: Int): List<HistoryResponse>  
    suspend fun updateStatus(historyId: Int, status: String): Unit
    suspend fun getByTutorIdWithDetails(tutorId: Int): List<Map<String, Any?>>
}