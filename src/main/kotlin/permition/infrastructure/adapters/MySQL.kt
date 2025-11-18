package permition.infrastructure.adapters

import core.ConnMySQL
import java.sql.Date
import java.sql.Timestamp
import permition.domain.PermitRepository
import permition.domain.entities.PermitReason
import permition.domain.entities.PermitStatus
import permition.domain.entities.PermitWithDetails
import permition.domain.entities.Permition
import permition.domain.entities.StudentInfo
import permition.domain.entities.TeacherInfo
import permition.domain.entities.TutorInfo

class MySQLPermitRepository(private val conn: ConnMySQL) : PermitRepository {

    override suspend fun save(permit: Permition): Permition {
        val query = """
            INSERT INTO permits (student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date, permit_document_url) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        return try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS).use { statement ->
                    statement.setInt(1, permit.studentId)
                    statement.setInt(2, permit.tutorId)
                    statement.setDate(3, Date.valueOf(permit.startDate))
                    statement.setDate(4, Date.valueOf(permit.endDate))
                    statement.setString(5, permit.reason.displayName)
                    statement.setString(6, permit.description)
                    statement.setInt(7, permit.cuatrimestre)
                    statement.setString(8, permit.evidence)
                    statement.setString(9, permit.status.name.lowercase())
                    statement.setTimestamp(10, Timestamp.valueOf(permit.requestDate))
                    statement.setString(11, permit.permitDocumentUrl)

                    statement.executeUpdate()

                    statement.generatedKeys.use { generatedKeys ->
                        if (generatedKeys.next()) {
                            permit.copy(permitId = generatedKeys.getInt(1))
                        } else {
                            throw Exception("Failed to get generated permit ID")
                        }
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to save permit: ${error.message}")
        }
    }

    override suspend fun getById(permitId: Int): Permition? {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date, permit_document_url
            FROM permits 
            WHERE permit_id = ?
        """

        return try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, permitId)

                    statement.executeQuery().use { resultSet ->
                        if (!resultSet.next()) {
                            null
                        } else {
                            Permition(
                                permitId = resultSet.getInt("permit_id"),
                                studentId = resultSet.getInt("student_id"),
                                tutorId = resultSet.getInt("tutor_id"),
                                startDate = resultSet.getDate("start_date").toLocalDate(),
                                endDate = resultSet.getDate("end_date").toLocalDate(),
                                reason = PermitReason.fromString(resultSet.getString("reason")),
                                description = resultSet.getString("description"),
                                cuatrimestre = resultSet.getInt("cuatrimestre"),
                                evidence = resultSet.getString("evidence"),
                                status = PermitStatus.fromString(resultSet.getString("status")),
                                requestDate = resultSet.getTimestamp("request_date").toLocalDateTime(),
                                permitDocumentUrl = resultSet.getString("permit_document_url")
                            )
                        }
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get permit by id: ${error.message}")
        }
    }

    override suspend fun getAll(): List<Permition> {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date, permit_document_url
            FROM permits 
            ORDER BY request_date DESC
        """

        return try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.executeQuery().use { resultSet ->
                        buildList {
                            while (resultSet.next()) {
                                add(
                                    Permition(
                                        permitId = resultSet.getInt("permit_id"),
                                        studentId = resultSet.getInt("student_id"),
                                        tutorId = resultSet.getInt("tutor_id"),
                                        startDate = resultSet.getDate("start_date").toLocalDate(),
                                        endDate = resultSet.getDate("end_date").toLocalDate(),
                                        reason = PermitReason.fromString(resultSet.getString("reason")),
                                        description = resultSet.getString("description"),
                                        cuatrimestre = resultSet.getInt("cuatrimestre"),
                                        evidence = resultSet.getString("evidence"),
                                        status = PermitStatus.fromString(resultSet.getString("status")),
                                        requestDate = resultSet.getTimestamp("request_date").toLocalDateTime(),
                                        permitDocumentUrl = resultSet.getString("permit_document_url")
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get all permits: ${error.message}")
        }
    }

    override suspend fun getByStudentId(studentId: Int): List<Permition> {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date, permit_document_url
            FROM permits 
            WHERE student_id = ?
            ORDER BY request_date DESC
        """

        return try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, studentId)
                    statement.executeQuery().use { resultSet ->
                        buildList {
                            while (resultSet.next()) {
                                add(
                                    Permition(
                                        permitId = resultSet.getInt("permit_id"),
                                        studentId = resultSet.getInt("student_id"),
                                        tutorId = resultSet.getInt("tutor_id"),
                                        startDate = resultSet.getDate("start_date").toLocalDate(),
                                        endDate = resultSet.getDate("end_date").toLocalDate(),
                                        reason = PermitReason.fromString(resultSet.getString("reason")),
                                        description = resultSet.getString("description"),
                                        cuatrimestre = resultSet.getInt("cuatrimestre"),
                                        evidence = resultSet.getString("evidence"),
                                        status = PermitStatus.fromString(resultSet.getString("status")),
                                        requestDate = resultSet.getTimestamp("request_date").toLocalDateTime(),
                                        permitDocumentUrl = resultSet.getString("permit_document_url")
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get permits by student: ${error.message}")
        }
    }

    override suspend fun getByTutorId(tutorId: Int): List<Permition> {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date, permit_document_url
            FROM permits 
            WHERE tutor_id = ?
            ORDER BY request_date DESC
        """

        return try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, tutorId)
                    statement.executeQuery().use { resultSet ->
                        buildList {
                            while (resultSet.next()) {
                                add(
                                    Permition(
                                        permitId = resultSet.getInt("permit_id"),
                                        studentId = resultSet.getInt("student_id"),
                                        tutorId = resultSet.getInt("tutor_id"),
                                        startDate = resultSet.getDate("start_date").toLocalDate(),
                                        endDate = resultSet.getDate("end_date").toLocalDate(),
                                        reason = PermitReason.fromString(resultSet.getString("reason")),
                                        description = resultSet.getString("description"),
                                        cuatrimestre = resultSet.getInt("cuatrimestre"),
                                        evidence = resultSet.getString("evidence"),
                                        status = PermitStatus.fromString(resultSet.getString("status")),
                                        requestDate = resultSet.getTimestamp("request_date").toLocalDateTime(),
                                        permitDocumentUrl = resultSet.getString("permit_document_url")
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get permits by tutor: ${error.message}")
        }
    }

    override suspend fun getByStatus(status: PermitStatus): List<Permition> {
        val query = """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date, permit_document_url
            FROM permits 
            WHERE status = ?
            ORDER BY request_date DESC
        """

        return try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, status.name.lowercase())
                    statement.executeQuery().use { resultSet ->
                        buildList {
                            while (resultSet.next()) {
                                add(
                                    Permition(
                                        permitId = resultSet.getInt("permit_id"),
                                        studentId = resultSet.getInt("student_id"),
                                        tutorId = resultSet.getInt("tutor_id"),
                                        startDate = resultSet.getDate("start_date").toLocalDate(),
                                        endDate = resultSet.getDate("end_date").toLocalDate(),
                                        reason = PermitReason.fromString(resultSet.getString("reason")),
                                        description = resultSet.getString("description"),
                                        cuatrimestre = resultSet.getInt("cuatrimestre"),
                                        evidence = resultSet.getString("evidence"),
                                        status = PermitStatus.fromString(resultSet.getString("status")),
                                        requestDate = resultSet.getTimestamp("request_date").toLocalDateTime(),
                                        permitDocumentUrl = resultSet.getString("permit_document_url")
                                    )
                                )
                            }
                        }
                    }
                }
            }
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
                cuatrimestre = ?,
                evidence = ?, 
                status = ?,
                permit_document_url = ?
            WHERE permit_id = ?
        """

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, permit.studentId)
                    statement.setInt(2, permit.tutorId)
                    statement.setDate(3, Date.valueOf(permit.startDate))
                    statement.setDate(4, Date.valueOf(permit.endDate))
                    statement.setString(5, permit.reason.displayName)
                    statement.setString(6, permit.description)
                    statement.setInt(7, permit.cuatrimestre)
                    statement.setString(8, permit.evidence)
                    statement.setString(9, permit.status.name.lowercase())
                    statement.setString(10, permit.permitDocumentUrl)
                    statement.setInt(11, permit.permitId!!)

                    val rowsAffected = statement.executeUpdate()

                    if (rowsAffected == 0) {
                        throw Exception("Permit not found")
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to update permit: ${error.message}")
        }
    }

    override suspend fun delete(permitId: Int) {
        val query = "DELETE FROM permits WHERE permit_id = ?"

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, permitId)

                    val rowsAffected = statement.executeUpdate()

                    if (rowsAffected == 0) {
                        throw Exception("Permit not found")
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to delete permit: ${error.message}")
        }
    }

    override suspend fun getAllWithDetails(): List<PermitWithDetails> {
        val query = """
        SELECT 
            p.permit_id,
            p.start_date,
            p.end_date,
            p.reason,
            p.description,
            p.cuatrimestre,
            p.evidence,
            p.status,
            p.request_date,
            p.permit_document_url,
            s.student_id,
            s.user_id as student_user_id,
            s.enrollment_number,
            CONCAT(su.first_name, ' ', COALESCE(su.middle_name, ''), ' ', su.last_name, ' ', COALESCE(su.second_last_name, '')) as student_full_name,
            su.email as student_email,
            su.phone as student_phone,
            t.tutor_id,
            t.user_id as tutor_user_id,
            CONCAT(tu.first_name, ' ', COALESCE(tu.middle_name, ''), ' ', tu.last_name, ' ', COALESCE(tu.second_last_name, '')) as tutor_full_name,
            tu.email as tutor_email,
            tu.phone as tutor_phone,
            te.teacher_id,
            te.user_id as teacher_user_id,
            CONCAT(teu.first_name, ' ', COALESCE(teu.middle_name, ''), ' ', teu.last_name, ' ', COALESCE(teu.second_last_name, '')) as teacher_full_name,
            teu.email as teacher_email,
            teu.phone as teacher_phone
        FROM permits p
        INNER JOIN students s ON p.student_id = s.student_id
        INNER JOIN users su ON s.user_id = su.user_id
        INNER JOIN tutors t ON p.tutor_id = t.tutor_id
        INNER JOIN users tu ON t.user_id = tu.user_id
        LEFT JOIN permits_teachers pt ON p.permit_id = pt.permit_id
        LEFT JOIN teachers te ON pt.teacher_id = te.teacher_id
        LEFT JOIN users teu ON te.user_id = teu.user_id
        ORDER BY p.request_date DESC, p.permit_id, te.teacher_id
        """

        return try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.executeQuery().use { resultSet ->
                        val permitsMap = mutableMapOf<Int, PermitWithDetails>()
                        
                        while (resultSet.next()) {
                            val permitId = resultSet.getInt("permit_id")
                            
                            if (permitsMap.containsKey(permitId)) {
                                val teacherId = resultSet.getInt("teacher_id")
                                if (!resultSet.wasNull() && teacherId > 0) {
                                    val teacher = TeacherInfo(
                                        teacherId = teacherId,
                                        userId = resultSet.getInt("teacher_user_id"),
                                        fullName = resultSet.getString("teacher_full_name").trim(),
                                        email = resultSet.getString("teacher_email"),
                                        phone = resultSet.getString("teacher_phone")
                                    )
                                    val existingPermit = permitsMap[permitId]!!
                                    permitsMap[permitId] = existingPermit.copy(
                                        teachers = existingPermit.teachers + teacher
                                    )
                                }
                            } else {
                                val teachers = mutableListOf<TeacherInfo>()
                                val teacherId = resultSet.getInt("teacher_id")
                                if (!resultSet.wasNull() && teacherId > 0) {
                                    teachers.add(
                                        TeacherInfo(
                                            teacherId = teacherId,
                                            userId = resultSet.getInt("teacher_user_id"),
                                            fullName = resultSet.getString("teacher_full_name").trim(),
                                            email = resultSet.getString("teacher_email"),
                                            phone = resultSet.getString("teacher_phone")
                                        )
                                    )
                                }
                                
                                permitsMap[permitId] = PermitWithDetails(
                                    permitId = permitId,
                                    studentInfo = StudentInfo(
                                        studentId = resultSet.getInt("student_id"),
                                        userId = resultSet.getInt("student_user_id"),
                                        fullName = resultSet.getString("student_full_name").trim(),
                                        email = resultSet.getString("student_email"),
                                        phone = resultSet.getString("student_phone"),
                                        enrollmentNumber = resultSet.getString("enrollment_number")
                                    ),
                                    tutorInfo = TutorInfo(
                                        tutorId = resultSet.getInt("tutor_id"),
                                        userId = resultSet.getInt("tutor_user_id"),
                                        fullName = resultSet.getString("tutor_full_name").trim(),
                                        email = resultSet.getString("tutor_email"),
                                        phone = resultSet.getString("tutor_phone")
                                    ),
                                    teachers = teachers,
                                    startDate = resultSet.getDate("start_date").toLocalDate(),
                                    endDate = resultSet.getDate("end_date").toLocalDate(),
                                    reason = PermitReason.fromString(resultSet.getString("reason")),
                                    description = resultSet.getString("description"),
                                    cuatrimestre = resultSet.getInt("cuatrimestre"),
                                    evidence = resultSet.getString("evidence"),
                                    status = PermitStatus.fromString(resultSet.getString("status")),
                                    requestDate = resultSet.getTimestamp("request_date").toLocalDateTime(),
                                    permitDocumentUrl = resultSet.getString("permit_document_url")
                                )
                            }
                        }
                        
                        permitsMap.values.toList()
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get all permits with details: ${error.message}")
        }
    }

    override suspend fun getByIdWithDetails(permitId: Int): PermitWithDetails? {
        val query = """
        SELECT 
            p.permit_id,
            p.start_date,
            p.end_date,
            p.reason,
            p.description,
            p.cuatrimestre,
            p.evidence,
            p.status,
            p.request_date,
            p.permit_document_url,
            s.student_id,
            s.user_id as student_user_id,
            s.enrollment_number,
            CONCAT(su.first_name, ' ', COALESCE(su.middle_name, ''), ' ', su.last_name, ' ', COALESCE(su.second_last_name, '')) as student_full_name,
            su.email as student_email,
            su.phone as student_phone,
            t.tutor_id,
            t.user_id as tutor_user_id,
            CONCAT(tu.first_name, ' ', COALESCE(tu.middle_name, ''), ' ', tu.last_name, ' ', COALESCE(tu.second_last_name, '')) as tutor_full_name,
            tu.email as tutor_email,
            tu.phone as tutor_phone,
            te.teacher_id,
            te.user_id as teacher_user_id,
            CONCAT(teu.first_name, ' ', COALESCE(teu.middle_name, ''), ' ', teu.last_name, ' ', COALESCE(teu.second_last_name, '')) as teacher_full_name,
            teu.email as teacher_email,
            teu.phone as teacher_phone
        FROM permits p
        INNER JOIN students s ON p.student_id = s.student_id
        INNER JOIN users su ON s.user_id = su.user_id
        INNER JOIN tutors t ON p.tutor_id = t.tutor_id
        INNER JOIN users tu ON t.user_id = tu.user_id
        LEFT JOIN permits_teachers pt ON p.permit_id = pt.permit_id
        LEFT JOIN teachers te ON pt.teacher_id = te.teacher_id
        LEFT JOIN users teu ON te.user_id = teu.user_id
        WHERE p.permit_id = ?
        ORDER BY te.teacher_id
        """

        return try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, permitId)

                    statement.executeQuery().use { resultSet ->
                        var permit: PermitWithDetails? = null
                        val teachers = mutableListOf<TeacherInfo>()
                        
                        while (resultSet.next()) {
                            if (permit == null) {
                                permit = PermitWithDetails(
                                    permitId = resultSet.getInt("permit_id"),
                                    studentInfo = StudentInfo(
                                        studentId = resultSet.getInt("student_id"),
                                        userId = resultSet.getInt("student_user_id"),
                                        fullName = resultSet.getString("student_full_name").trim(),
                                        email = resultSet.getString("student_email"),
                                        phone = resultSet.getString("student_phone"),
                                        enrollmentNumber = resultSet.getString("enrollment_number")
                                    ),
                                    tutorInfo = TutorInfo(
                                        tutorId = resultSet.getInt("tutor_id"),
                                        userId = resultSet.getInt("tutor_user_id"),
                                        fullName = resultSet.getString("tutor_full_name").trim(),
                                        email = resultSet.getString("tutor_email"),
                                        phone = resultSet.getString("tutor_phone")
                                    ),
                                    teachers = emptyList(), 
                                    startDate = resultSet.getDate("start_date").toLocalDate(),
                                    endDate = resultSet.getDate("end_date").toLocalDate(),
                                    reason = PermitReason.fromString(resultSet.getString("reason")),
                                    description = resultSet.getString("description"),
                                    cuatrimestre = resultSet.getInt("cuatrimestre"),
                                    evidence = resultSet.getString("evidence"),
                                    status = PermitStatus.fromString(resultSet.getString("status")),
                                    requestDate = resultSet.getTimestamp("request_date").toLocalDateTime(),
                                    permitDocumentUrl = resultSet.getString("permit_document_url")
                                )
                            }
                            
                            val teacherId = resultSet.getInt("teacher_id")
                            if (!resultSet.wasNull() && teacherId > 0) {
                                teachers.add(
                                    TeacherInfo(
                                        teacherId = teacherId,
                                        userId = resultSet.getInt("teacher_user_id"),
                                        fullName = resultSet.getString("teacher_full_name").trim(),
                                        email = resultSet.getString("teacher_email"),
                                        phone = resultSet.getString("teacher_phone")
                                    )
                                )
                            }
                        }
                        
                        permit?.copy(teachers = teachers)
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get permit by id with details: ${error.message}")
        }
    }

    override suspend fun savePermitTeachers(permitId: Int, teacherIds: List<Int>) {
        if (teacherIds.isEmpty()) return

        val query = "INSERT INTO permits_teachers (permit_id, teacher_id) VALUES (?, ?)"

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    teacherIds.forEach { teacherId ->
                        statement.setInt(1, permitId)
                        statement.setInt(2, teacherId)
                        statement.addBatch()
                    }

                    statement.executeBatch()
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to save permit teachers: ${error.message}")
        }
    }

    override suspend fun updatePermitTeachers(permitId: Int, teacherIds: List<Int>) {
        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement("DELETE FROM permits_teachers WHERE permit_id = ?").use { deleteStatement ->
                    deleteStatement.setInt(1, permitId)
                    deleteStatement.executeUpdate()
                }

                if (teacherIds.isNotEmpty()) {
                    connection.prepareStatement("INSERT INTO permits_teachers (permit_id, teacher_id) VALUES (?, ?)").use { insertStatement ->
                        teacherIds.forEach { teacherId ->
                            insertStatement.setInt(1, permitId)
                            insertStatement.setInt(2, teacherId)
                            insertStatement.addBatch()
                        }

                        insertStatement.executeBatch()
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to update permit teachers: ${error.message}")
        }
    }

    override suspend fun updatePermitDocumentUrl(permitId: Int, documentUrl: String) {
        val query = "UPDATE permits SET permit_document_url = ? WHERE permit_id = ?"
        
        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, documentUrl)
                    statement.setInt(2, permitId)
                    
                    val rowsAffected = statement.executeUpdate()
                    
                    if (rowsAffected == 0) {
                        throw Exception("Permit not found")
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to update permit document URL: ${error.message}")
        }
    }
}