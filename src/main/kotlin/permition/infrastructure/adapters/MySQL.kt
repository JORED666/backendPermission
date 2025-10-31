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
        val query =
                """
            INSERT INTO permits (student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        try {
            val connection = conn.getConnection()
            val statement =
                    connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)

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
        val query =
                """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date 
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
                    cuatrimestre = resultSet.getInt("cuatrimestre"),
                    evidence = resultSet.getString("evidence"),
                    status = PermitStatus.fromString(resultSet.getString("status")),
                    requestDate = resultSet.getTimestamp("request_date").toLocalDateTime()
            )
        } catch (error: Exception) {
            throw Exception("Failed to get permit by id: ${error.message}")
        }
    }

    override suspend fun getAll(): List<Permition> {
        val query =
                """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date 
            FROM permits 
            ORDER BY request_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            val permits = mutableListOf<Permition>()

            while (resultSet.next()) {
                permits.add(
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
                                requestDate =
                                        resultSet.getTimestamp("request_date").toLocalDateTime()
                        )
                )
            }

            return permits
        } catch (error: Exception) {
            throw Exception("Failed to get all permits: ${error.message}")
        }
    }

    override suspend fun getByStudentId(studentId: Int): List<Permition> {
        val query =
                """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date 
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
                permits.add(
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
                                requestDate =
                                        resultSet.getTimestamp("request_date").toLocalDateTime()
                        )
                )
            }

            return permits
        } catch (error: Exception) {
            throw Exception("Failed to get permits by student: ${error.message}")
        }
    }

    override suspend fun getByTutorId(tutorId: Int): List<Permition> {
        val query =
                """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date 
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
                permits.add(
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
                                requestDate =
                                        resultSet.getTimestamp("request_date").toLocalDateTime()
                        )
                )
            }

            return permits
        } catch (error: Exception) {
            throw Exception("Failed to get permits by tutor: ${error.message}")
        }
    }

    override suspend fun getByStatus(status: PermitStatus): List<Permition> {
        val query =
                """
            SELECT permit_id, student_id, tutor_id, start_date, end_date, reason, description, cuatrimestre, evidence, status, request_date 
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
                permits.add(
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
                                requestDate =
                                        resultSet.getTimestamp("request_date").toLocalDateTime()
                        )
                )
            }

            return permits
        } catch (error: Exception) {
            throw Exception("Failed to get permits by status: ${error.message}")
        }
    }

    override suspend fun update(permit: Permition) {
        val query =
                """
            UPDATE permits 
            SET student_id = ?, 
                tutor_id = ?, 
                start_date = ?, 
                end_date = ?, 
                reason = ?, 
                description = ?, 
                cuatrimestre = ?,
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
            statement.setInt(7, permit.cuatrimestre)
            statement.setString(8, permit.evidence)
            statement.setString(9, permit.status.name.lowercase())
            statement.setInt(10, permit.permitId!!)

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

    override suspend fun getAllWithDetails(): List<PermitWithDetails> {
        val query =
                """
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
            tu.phone as tutor_phone
        FROM permits p
        INNER JOIN students s ON p.student_id = s.student_id
        INNER JOIN users su ON s.user_id = su.user_id
        INNER JOIN tutors t ON p.tutor_id = t.tutor_id
        INNER JOIN users tu ON t.user_id = tu.user_id
        ORDER BY p.request_date DESC
    """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            val permits = mutableListOf<PermitWithDetails>()

            while (resultSet.next()) {
                val permitId = resultSet.getInt("permit_id")

                val teachers = getTeachersByPermitId(permitId)

                permits.add(
                        PermitWithDetails(
                                permitId = permitId,
                                studentInfo =
                                        StudentInfo(
                                                studentId = resultSet.getInt("student_id"),
                                                userId = resultSet.getInt("student_user_id"),
                                                fullName =
                                                        resultSet
                                                                .getString("student_full_name")
                                                                .trim(),
                                                email = resultSet.getString("student_email"),
                                                phone = resultSet.getString("student_phone"),
                                                enrollmentNumber =
                                                        resultSet.getString("enrollment_number")
                                        ),
                                tutorInfo =
                                        TutorInfo(
                                                tutorId = resultSet.getInt("tutor_id"),
                                                userId = resultSet.getInt("tutor_user_id"),
                                                fullName =
                                                        resultSet
                                                                .getString("tutor_full_name")
                                                                .trim(),
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
                                requestDate =
                                        resultSet.getTimestamp("request_date").toLocalDateTime()
                        )
                )
            }

            return permits
        } catch (error: Exception) {
            throw Exception("Failed to get all permits with details: ${error.message}")
        }
    }

    override suspend fun getByIdWithDetails(permitId: Int): PermitWithDetails? {
        val query =
                """
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
            tu.phone as tutor_phone
        FROM permits p
        INNER JOIN students s ON p.student_id = s.student_id
        INNER JOIN users su ON s.user_id = su.user_id
        INNER JOIN tutors t ON p.tutor_id = t.tutor_id
        INNER JOIN users tu ON t.user_id = tu.user_id
        WHERE p.permit_id = ?
    """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, permitId)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            val teachers = getTeachersByPermitId(permitId)

            return PermitWithDetails(
                    permitId = resultSet.getInt("permit_id"),
                    studentInfo =
                            StudentInfo(
                                    studentId = resultSet.getInt("student_id"),
                                    userId = resultSet.getInt("student_user_id"),
                                    fullName = resultSet.getString("student_full_name").trim(),
                                    email = resultSet.getString("student_email"),
                                    phone = resultSet.getString("student_phone"),
                                    enrollmentNumber = resultSet.getString("enrollment_number")
                            ),
                    tutorInfo =
                            TutorInfo(
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
                    requestDate = resultSet.getTimestamp("request_date").toLocalDateTime()
            )
        } catch (error: Exception) {
            throw Exception("Failed to get permit by id with details: ${error.message}")
        }
    }

    private suspend fun getTeachersByPermitId(permitId: Int): List<TeacherInfo> {
        val query =
                """
        SELECT 
            te.teacher_id,
            te.user_id,
            CONCAT(u.first_name, ' ', COALESCE(u.middle_name, ''), ' ', u.last_name, ' ', COALESCE(u.second_last_name, '')) as teacher_full_name,
            u.email,
            u.phone
        FROM permits_teachers pt
        INNER JOIN teachers te ON pt.teacher_id = te.teacher_id
        INNER JOIN users u ON te.user_id = u.user_id
        WHERE pt.permit_id = ?
    """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, permitId)

            val resultSet = statement.executeQuery()
            val teachers = mutableListOf<TeacherInfo>()

            while (resultSet.next()) {
                teachers.add(
                        TeacherInfo(
                                teacherId = resultSet.getInt("teacher_id"),
                                userId = resultSet.getInt("user_id"),
                                fullName = resultSet.getString("teacher_full_name").trim(),
                                email = resultSet.getString("email"),
                                phone = resultSet.getString("phone")
                        )
                )
            }

            return teachers
        } catch (error: Exception) {
            throw Exception("Failed to get teachers for permit: ${error.message}")
        }
    }

    override suspend fun savePermitTeachers(permitId: Int, teacherIds: List<Int>) {
        if (teacherIds.isEmpty()) return

        val query = "INSERT INTO permits_teachers (permit_id, teacher_id) VALUES (?, ?)"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)

            teacherIds.forEach { teacherId ->
                statement.setInt(1, permitId)
                statement.setInt(2, teacherId)
                statement.addBatch()
            }

            statement.executeBatch()
        } catch (error: Exception) {
            throw Exception("Failed to save permit teachers: ${error.message}")
        }
    }

    override suspend fun updatePermitTeachers(permitId: Int, teacherIds: List<Int>) {
        try {
            val connection = conn.getConnection()

            val deleteQuery = "DELETE FROM permits_teachers WHERE permit_id = ?"
            val deleteStatement = connection.prepareStatement(deleteQuery)
            deleteStatement.setInt(1, permitId)
            deleteStatement.executeUpdate()

            if (teacherIds.isNotEmpty()) {
                val insertQuery =
                        "INSERT INTO permits_teachers (permit_id, teacher_id) VALUES (?, ?)"
                val insertStatement = connection.prepareStatement(insertQuery)

                teacherIds.forEach { teacherId ->
                    insertStatement.setInt(1, permitId)
                    insertStatement.setInt(2, teacherId)
                    insertStatement.addBatch()
                }

                insertStatement.executeBatch()
            }
        } catch (error: Exception) {
            throw Exception("Failed to update permit teachers: ${error.message}")
        }
    }
}
