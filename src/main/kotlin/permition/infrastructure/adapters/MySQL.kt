package permition.infrastructure.adapters

import permition.domain.PermitRepository
import permition.domain.entities.Permition
import permition.domain.entities.PermitReason
import permition.domain.entities.PermitStatus
import core.ConnMySQL
import java.sql.Date
import java.sql.Timestamp

class MySQLPermitRepository(private val conn: ConnMySQL) : PermitRepository {
    
    override suspend fun save(permit: Permition): Permition {
        val query = """
            INSERT INTO permits (student_id, tutor_id, start_date, end_date, reason, description, evidence, status, request_date) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
            
            statement.setInt(1, permit.studentId)
            statement.setInt(2, permit.tutorId)
            statement.setDate(3, Date.valueOf(permit.startDate))
            statement.setDate(4, Date.valueOf(permit.endDate))
            statement.setString(5, permit.reason.displayName)
            statement.setString(6, permit.description)
            statement.setString(7, permit.evidence)
            statement.setString(8, permit.status.name.lowercase())
            statement.setTimestamp(9, Timestamp.valueOf(permit.requestDate))
            
            statement.executeUpdate()
            
            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) {
                val id = generatedKeys.getInt(1)
                return permit.copy(permitId = id)
            }
            
            throw Exception("Failed to get generated permit ID")
        } catch (error: Exception) {
            throw Exception("Failed to save permit: ${error.message}")
        }
    }

    override suspend fun getById(permitId: Int): Permition? {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, evidence, status, request_date 
            FROM permits 
            WHERE permit_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, permitId)
            
            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return Permition(
                permitId = resultSet.getInt("permit_id"),
                studentId = resultSet.getInt("student_id"),
                tutorId = resultSet.getInt("tutor_id"),
                startDate = resultSet.getDate("start_date").toLocalDate(),
                endDate = resultSet.getDate("end_date").toLocalDate(),
                reason = PermitReason.fromString(resultSet.getString("reason")),
                description = resultSet.getString("description"),
                evidence = resultSet.getString("evidence"),
                status = PermitStatus.fromString(resultSet.getString("status")),
                requestDate = resultSet.getTimestamp("request_date").toLocalDateTime()
            )
        } catch (error: Exception) {
            throw Exception("Failed to get permit by id: ${error.message}")
        }
    }

    override suspend fun getAll(): List<Permition> {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, evidence, status, request_date 
            FROM permits 
            ORDER BY request_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            
            val permits = mutableListOf<Permition>()
            
            while (resultSet.next()) {
                permits.add(Permition(
                    permitId = resultSet.getInt("permit_id"),
                    studentId = resultSet.getInt("student_id"),
                    tutorId = resultSet.getInt("tutor_id"),
                    startDate = resultSet.getDate("start_date").toLocalDate(),
                    endDate = resultSet.getDate("end_date").toLocalDate(),
                    reason = PermitReason.fromString(resultSet.getString("reason")),
                    description = resultSet.getString("description"),
                    evidence = resultSet.getString("evidence"),
                    status = PermitStatus.fromString(resultSet.getString("status")),
                    requestDate = resultSet.getTimestamp("request_date").toLocalDateTime()
                ))
            }
            
            return permits
        } catch (error: Exception) {
            throw Exception("Failed to get all permits: ${error.message}")
        }
    }

    override suspend fun getByStudentId(studentId: Int): List<Permition> {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, evidence, status, request_date 
            FROM permits 
            WHERE student_id = ?
            ORDER BY request_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, studentId)
            val resultSet = statement.executeQuery()
            
            val permits = mutableListOf<Permition>()
            
            while (resultSet.next()) {
                permits.add(Permition(
                    permitId = resultSet.getInt("permit_id"),
                    studentId = resultSet.getInt("student_id"),
                    tutorId = resultSet.getInt("tutor_id"),
                    startDate = resultSet.getDate("start_date").toLocalDate(),
                    endDate = resultSet.getDate("end_date").toLocalDate(),
                    reason = PermitReason.fromString(resultSet.getString("reason")),
                    description = resultSet.getString("description"),
                    evidence = resultSet.getString("evidence"),
                    status = PermitStatus.fromString(resultSet.getString("status")),
                    requestDate = resultSet.getTimestamp("request_date").toLocalDateTime()
                ))
            }
            
            return permits
        } catch (error: Exception) {
            throw Exception("Failed to get permits by student: ${error.message}")
        }
    }

    override suspend fun getByTutorId(tutorId: Int): List<Permition> {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, evidence, status, request_date 
            FROM permits 
            WHERE tutor_id = ?
            ORDER BY request_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, tutorId)
            val resultSet = statement.executeQuery()
            
            val permits = mutableListOf<Permition>()
            
            while (resultSet.next()) {
                permits.add(Permition(
                    permitId = resultSet.getInt("permit_id"),
                    studentId = resultSet.getInt("student_id"),
                    tutorId = resultSet.getInt("tutor_id"),
                    startDate = resultSet.getDate("start_date").toLocalDate(),
                    endDate = resultSet.getDate("end_date").toLocalDate(),
                    reason = PermitReason.fromString(resultSet.getString("reason")),
                    description = resultSet.getString("description"),
                    evidence = resultSet.getString("evidence"),
                    status = PermitStatus.fromString(resultSet.getString("status")),
                    requestDate = resultSet.getTimestamp("request_date").toLocalDateTime()
                ))
            }
            
            return permits
        } catch (error: Exception) {
            throw Exception("Failed to get permits by tutor: ${error.message}")
        }
    }

    override suspend fun getByStatus(status: PermitStatus): List<Permition> {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, evidence, status, request_date 
            FROM permits 
            WHERE status = ?
            ORDER BY request_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setString(1, status.name.lowercase())
            val resultSet = statement.executeQuery()
            
            val permits = mutableListOf<Permition>()
            
            while (resultSet.next()) {
                permits.add(Permition(
                    permitId = resultSet.getInt("permit_id"),
                    studentId = resultSet.getInt("student_id"),
                    tutorId = resultSet.getInt("tutor_id"),
                    startDate = resultSet.getDate("start_date").toLocalDate(),
                    endDate = resultSet.getDate("end_date").toLocalDate(),
                    reason = PermitReason.fromString(resultSet.getString("reason")),
                    description = resultSet.getString("description"),
                    evidence = resultSet.getString("evidence"),
                    status = PermitStatus.fromString(resultSet.getString("status")),
                    requestDate = resultSet.getTimestamp("request_date").toLocalDateTime()
                ))
            }
            
            return permits
        } catch (error: Exception) {
            throw Exception("Failed to get permits by status: ${error.message}")
        }
    }

    override suspend fun update(permit: Permition) {
        val query = """
            UPDATE permits 
            SET student_id = ?, 
                tutor_id = ?, 
                start_date = ?, 
                end_date = ?, 
                reason = ?, 
                description = ?, 
                evidence = ?, 
                status = ? 
            WHERE permit_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            
            statement.setInt(1, permit.studentId)
            statement.setInt(2, permit.tutorId)
            statement.setDate(3, Date.valueOf(permit.startDate))
            statement.setDate(4, Date.valueOf(permit.endDate))
            statement.setString(5, permit.reason.displayName)
            statement.setString(6, permit.description)
            statement.setString(7, permit.evidence)
            statement.setString(8, permit.status.name.lowercase())
            statement.setInt(9, permit.permitId!!)
            
            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("Permit not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to update permit: ${error.message}")
        }
    }

    override suspend fun delete(permitId: Int) {
        val query = "DELETE FROM permits WHERE permit_id = ?"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, permitId)
            
            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("Permit not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to delete permit: ${error.message}")
        }
    }
}