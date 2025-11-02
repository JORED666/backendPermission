package history.infrastructure.adapters

import history.domain.IHistoryRepository
import history.domain.entities.History
import core.ConnMySQL
import java.sql.Date

class MySQLHistoryRepository(private val conn: ConnMySQL) : IHistoryRepository {
    
    override suspend fun save(history: History): History {
        val query = """
            INSERT INTO permit_history (permit_id, student_id, start_date, end_date, reason, status) 
            VALUES (?, ?, ?, ?, ?, ?)
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
            
            statement.setInt(1, history.permitId)
            statement.setInt(2, history.studentId)
            
            if (history.startDate != null) {
                statement.setDate(3, Date.valueOf(history.startDate))
            } else {
                statement.setNull(3, java.sql.Types.DATE)
            }
            
            if (history.endDate != null) {
                statement.setDate(4, Date.valueOf(history.endDate))
            } else {
                statement.setNull(4, java.sql.Types.DATE)
            }
            
            statement.setString(5, history.reason)
            statement.setString(6, history.status)
            
            statement.executeUpdate()
            
            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) {
                val id = generatedKeys.getInt(1)
                return history.copy(historyId = id)
            }
            
            throw Exception("Failed to get generated history ID")
        } catch (error: Exception) {
            throw Exception("Failed to save history: ${error.message}")
        }
    }

    override suspend fun getById(historyId: Int): History? {
        val query = """
            SELECT history_id, permit_id, student_id, start_date, end_date, 
                   reason, status, request_date
            FROM permit_history 
            WHERE history_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, historyId)
            
            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return History(
                historyId = resultSet.getInt("history_id"),
                permitId = resultSet.getInt("permit_id"),
                studentId = resultSet.getInt("student_id"),
                startDate = resultSet.getDate("start_date")?.toLocalDate(),
                endDate = resultSet.getDate("end_date")?.toLocalDate(),
                reason = resultSet.getString("reason"),
                status = resultSet.getString("status"),
                requestDate = resultSet.getTimestamp("request_date")?.toLocalDateTime()
            )
        } catch (error: Exception) {
            throw Exception("Failed to get history by id: ${error.message}")
        }
    }

    override suspend fun getAll(): List<History> {
        val query = """
            SELECT history_id, permit_id, student_id, start_date, end_date, 
                   reason, status, request_date
            FROM permit_history 
            ORDER BY request_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            
            val histories = mutableListOf<History>()
            
            while (resultSet.next()) {
                histories.add(History(
                    historyId = resultSet.getInt("history_id"),
                    permitId = resultSet.getInt("permit_id"),
                    studentId = resultSet.getInt("student_id"),
                    startDate = resultSet.getDate("start_date")?.toLocalDate(),
                    endDate = resultSet.getDate("end_date")?.toLocalDate(),
                    reason = resultSet.getString("reason"),
                    status = resultSet.getString("status"),
                    requestDate = resultSet.getTimestamp("request_date")?.toLocalDateTime()
                ))
            }
            
            return histories
        } catch (error: Exception) {
            throw Exception("Failed to get all histories: ${error.message}")
        }
    }

    override suspend fun getAllWithDetails(): List<Map<String, Any?>> {
        val query = """
            SELECT 
                ph.history_id,
                ph.permit_id,
                ph.student_id,
                ph.start_date,
                ph.end_date,
                ph.reason,
                ph.status,
                ph.request_date,
                p.description AS permit_description,
                p.evidence AS permit_evidence,
                p.cuatrimestre AS permit_cuatrimestre,
                s.enrollment_number,
                CONCAT(u.first_name, ' ', COALESCE(u.middle_name, ''), ' ', u.last_name, ' ', COALESCE(u.second_last_name, '')) AS student_name,
                u.email AS student_email
            FROM permit_history ph
            INNER JOIN permits p ON ph.permit_id = p.permit_id
            INNER JOIN students s ON ph.student_id = s.student_id
            INNER JOIN users u ON s.user_id = u.user_id
            ORDER BY ph.request_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            
            val histories = mutableListOf<Map<String, Any?>>()
            
            while (resultSet.next()) {
                histories.add(mapOf(
                    "history_id" to resultSet.getInt("history_id"),
                    "permit_id" to resultSet.getInt("permit_id"),
                    "student_id" to resultSet.getInt("student_id"),
                    "start_date" to resultSet.getDate("start_date")?.toLocalDate(),
                    "end_date" to resultSet.getDate("end_date")?.toLocalDate(),
                    "reason" to resultSet.getString("reason"),
                    "status" to resultSet.getString("status"),
                    "request_date" to resultSet.getTimestamp("request_date")?.toLocalDateTime(),
                    "permit_description" to resultSet.getString("permit_description"),
                    "permit_evidence" to resultSet.getString("permit_evidence"),
                    "permit_cuatrimestre" to resultSet.getInt("permit_cuatrimestre"),
                    "enrollment_number" to resultSet.getString("enrollment_number"),
                    "student_name" to resultSet.getString("student_name"),
                    "student_email" to resultSet.getString("student_email")
                ))
            }
            
            return histories
        } catch (error: Exception) {
            throw Exception("Failed to get all histories with details: ${error.message}")
        }
    }

    override suspend fun getByIdWithDetails(historyId: Int): Map<String, Any?>? {
        val query = """
            SELECT 
                ph.history_id,
                ph.permit_id,
                ph.student_id,
                ph.start_date,
                ph.end_date,
                ph.reason,
                ph.status,
                ph.request_date,
                p.description AS permit_description,
                p.evidence AS permit_evidence,
                p.cuatrimestre AS permit_cuatrimestre,
                s.enrollment_number,
                CONCAT(u.first_name, ' ', COALESCE(u.middle_name, ''), ' ', u.last_name, ' ', COALESCE(u.second_last_name, '')) AS student_name,
                u.email AS student_email
            FROM permit_history ph
            INNER JOIN permits p ON ph.permit_id = p.permit_id
            INNER JOIN students s ON ph.student_id = s.student_id
            INNER JOIN users u ON s.user_id = u.user_id
            WHERE ph.history_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, historyId)
            
            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return mapOf(
                "history_id" to resultSet.getInt("history_id"),
                "permit_id" to resultSet.getInt("permit_id"),
                "student_id" to resultSet.getInt("student_id"),
                "start_date" to resultSet.getDate("start_date")?.toLocalDate(),
                "end_date" to resultSet.getDate("end_date")?.toLocalDate(),
                "reason" to resultSet.getString("reason"),
                "status" to resultSet.getString("status"),
                "request_date" to resultSet.getTimestamp("request_date")?.toLocalDateTime(),
                "permit_description" to resultSet.getString("permit_description"),
                "permit_evidence" to resultSet.getString("permit_evidence"),
                "permit_cuatrimestre" to resultSet.getInt("permit_cuatrimestre"),
                "enrollment_number" to resultSet.getString("enrollment_number"),
                "student_name" to resultSet.getString("student_name"),
                "student_email" to resultSet.getString("student_email")
            )
        } catch (error: Exception) {
            throw Exception("Failed to get history by id with details: ${error.message}")
        }
    }

    override suspend fun getByStudentId(studentId: Int): List<History> {
        val query = """
            SELECT history_id, permit_id, student_id, start_date, end_date, 
                   reason, status, request_date
            FROM permit_history 
            WHERE student_id = ?
            ORDER BY request_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, studentId)
            
            val resultSet = statement.executeQuery()
            
            val histories = mutableListOf<History>()
            
            while (resultSet.next()) {
                histories.add(History(
                    historyId = resultSet.getInt("history_id"),
                    permitId = resultSet.getInt("permit_id"),
                    studentId = resultSet.getInt("student_id"),
                    startDate = resultSet.getDate("start_date")?.toLocalDate(),
                    endDate = resultSet.getDate("end_date")?.toLocalDate(),
                    reason = resultSet.getString("reason"),
                    status = resultSet.getString("status"),
                    requestDate = resultSet.getTimestamp("request_date")?.toLocalDateTime()
                ))
            }
            
            return histories
        } catch (error: Exception) {
            throw Exception("Failed to get histories by student id: ${error.message}")
        }
    }

    override suspend fun getByStudentIdWithDetails(studentId: Int): List<Map<String, Any?>> {
        val query = """
            SELECT 
                ph.history_id,
                ph.permit_id,
                ph.student_id,
                ph.start_date,
                ph.end_date,
                ph.reason,
                ph.status,
                ph.request_date,
                p.description AS permit_description,
                p.evidence AS permit_evidence,
                p.cuatrimestre AS permit_cuatrimestre,
                s.enrollment_number,
                CONCAT(u.first_name, ' ', COALESCE(u.middle_name, ''), ' ', u.last_name, ' ', COALESCE(u.second_last_name, '')) AS student_name,
                u.email AS student_email
            FROM permit_history ph
            INNER JOIN permits p ON ph.permit_id = p.permit_id
            INNER JOIN students s ON ph.student_id = s.student_id
            INNER JOIN users u ON s.user_id = u.user_id
            WHERE ph.student_id = ?
            ORDER BY ph.request_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, studentId)
            
            val resultSet = statement.executeQuery()
            
            val histories = mutableListOf<Map<String, Any?>>()
            
            while (resultSet.next()) {
                histories.add(mapOf(
                    "history_id" to resultSet.getInt("history_id"),
                    "permit_id" to resultSet.getInt("permit_id"),
                    "student_id" to resultSet.getInt("student_id"),
                    "start_date" to resultSet.getDate("start_date")?.toLocalDate(),
                    "end_date" to resultSet.getDate("end_date")?.toLocalDate(),
                    "reason" to resultSet.getString("reason"),
                    "status" to resultSet.getString("status"),
                    "request_date" to resultSet.getTimestamp("request_date")?.toLocalDateTime(),
                    "permit_description" to resultSet.getString("permit_description"),
                    "permit_evidence" to resultSet.getString("permit_evidence"),
                    "permit_cuatrimestre" to resultSet.getInt("permit_cuatrimestre"),
                    "enrollment_number" to resultSet.getString("enrollment_number"),
                    "student_name" to resultSet.getString("student_name"),
                    "student_email" to resultSet.getString("student_email")
                ))
            }
            
            return histories
        } catch (error: Exception) {
            throw Exception("Failed to get histories by student id with details: ${error.message}")
        }
    }

    override suspend fun updateStatus(historyId: Int, status: String) {
        val query = """
            UPDATE permit_history 
            SET status = ? 
            WHERE history_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            
            statement.setString(1, status)
            statement.setInt(2, historyId)
            
            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("History not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to update history status: ${error.message}")
        }
    }
}