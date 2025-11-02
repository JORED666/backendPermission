package students.infrastructure.adapters

import core.ConnMySQL
import students.domain.IStudentRepository
import students.domain.entities.Student

class MySQLStudentRepository(private val conn: ConnMySQL) : IStudentRepository {

    override suspend fun save(student: Student): Student {
        val query =
                """
            INSERT INTO students (enrollment_number, family_tutor_phone, user_id, tutor_id) 
            VALUES (?, ?, ?, ?)
        """

        try {
            val connection = conn.getConnection()
            val statement =
                    connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)

            statement.setString(1, student.enrollmentNumber)
            statement.setString(2, student.familyTutorPhone)
            statement.setInt(3, student.userId)
            if (student.tutorId != null) {
                statement.setInt(4, student.tutorId)
            } else {
                statement.setNull(4, java.sql.Types.INTEGER)
            }

            statement.executeUpdate()

            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) {
                val id = generatedKeys.getInt(1)
                return student.copy(studentId = id)
            }

            throw Exception("Failed to get generated student ID")
        } catch (error: Exception) {
            throw Exception("Failed to save student: ${error.message}")
        }
    }

    override suspend fun getById(studentId: Int): Student? {
        val query =
                """
            SELECT student_id, enrollment_number, family_tutor_phone, user_id, tutor_id
            FROM students 
            WHERE student_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, studentId)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return Student(
                    studentId = resultSet.getInt("student_id"),
                    enrollmentNumber = resultSet.getString("enrollment_number"),
                    familyTutorPhone = resultSet.getString("family_tutor_phone"),
                    userId = resultSet.getInt("user_id"),
                    tutorId = resultSet.getObject("tutor_id") as? Int
            )
        } catch (error: Exception) {
            throw Exception("Failed to get student by id: ${error.message}")
        }
    }

    override suspend fun getAll(): List<Student> {
        val query =
                """
            SELECT student_id, enrollment_number, family_tutor_phone, user_id, tutor_id
            FROM students 
            ORDER BY student_id DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            val students = mutableListOf<Student>()

            while (resultSet.next()) {
                students.add(
                        Student(
                                studentId = resultSet.getInt("student_id"),
                                enrollmentNumber = resultSet.getString("enrollment_number"),
                                familyTutorPhone = resultSet.getString("family_tutor_phone"),
                                userId = resultSet.getInt("user_id"),
                                tutorId = resultSet.getObject("tutor_id") as? Int
                        )
                )
            }

            return students
        } catch (error: Exception) {
            throw Exception("Failed to get all students: ${error.message}")
        }
    }

    override suspend fun getAllWithDetails(): List<Map<String, Any?>> {
        val query =
                """
        SELECT 
            s.student_id,
            s.enrollment_number,
            s.family_tutor_phone,
            s.user_id,
            s.tutor_id,
            CONCAT(
                COALESCE(u.first_name, ''), 
                ' ', 
                COALESCE(u.middle_name, ''), 
                ' ', 
                COALESCE(u.last_name, ''), 
                ' ', 
                COALESCE(u.second_last_name, '')
            ) AS full_name,
            u.email,
            u.phone,
            r.role_name,
            r.description AS role_description,
            u.registration_date
        FROM students s
        INNER JOIN users u ON s.user_id = u.user_id
        INNER JOIN roles r ON u.role_id = r.role_id
        ORDER BY s.student_id DESC
    """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            val students = mutableListOf<Map<String, Any?>>()

            while (resultSet.next()) {
                students.add(
                        mapOf(
                                "student_id" to resultSet.getInt("student_id"),
                                "enrollment_number" to resultSet.getString("enrollment_number"),
                                "family_tutor_phone" to resultSet.getString("family_tutor_phone"),
                                "user_id" to resultSet.getInt("user_id"),
                                "tutor_id" to resultSet.getObject("tutor_id"),
                                "full_name" to (resultSet.getString("full_name") ?: ""),
                                "email" to (resultSet.getString("email") ?: ""),
                                "phone" to resultSet.getString("phone"),
                                "role_name" to (resultSet.getString("role_name") ?: ""),
                                "role_description" to resultSet.getString("role_description"),
                                "registration_date" to
                                        resultSet
                                                .getTimestamp("registration_date")
                                                .toLocalDateTime()
                        )
                )
            }

            return students
        } catch (error: Exception) {
            throw Exception("Failed to get all students with details: ${error.message}")
        }
    }

    override suspend fun getByIdWithDetails(studentId: Int): Map<String, Any?>? {
        val query =
                """
        SELECT 
            s.student_id,
            s.enrollment_number,
            s.family_tutor_phone,
            s.user_id,
            s.tutor_id,
            CONCAT(
                COALESCE(u.first_name, ''), 
                ' ', 
                COALESCE(u.middle_name, ''), 
                ' ', 
                COALESCE(u.last_name, ''), 
                ' ', 
                COALESCE(u.second_last_name, '')
            ) AS full_name,
            u.email,
            u.phone,
            r.role_name,
            r.description AS role_description,
            u.registration_date
        FROM students s
        INNER JOIN users u ON s.user_id = u.user_id
        INNER JOIN roles r ON u.role_id = r.role_id
        WHERE s.student_id = ?
    """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, studentId)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return mapOf(
                    "student_id" to resultSet.getInt("student_id"),
                    "enrollment_number" to resultSet.getString("enrollment_number"),
                    "family_tutor_phone" to resultSet.getString("family_tutor_phone"),
                    "user_id" to resultSet.getInt("user_id"),
                    "tutor_id" to resultSet.getObject("tutor_id"),
                    "full_name" to (resultSet.getString("full_name") ?: ""),
                    "email" to (resultSet.getString("email") ?: ""),
                    "phone" to resultSet.getString("phone"),
                    "role_name" to (resultSet.getString("role_name") ?: ""),
                    "role_description" to resultSet.getString("role_description"),
                    "registration_date" to
                            resultSet.getTimestamp("registration_date").toLocalDateTime()
            )
        } catch (error: Exception) {
            throw Exception("Failed to get student by id with details: ${error.message}")
        }
    }

    override suspend fun getByUserId(userId: Int): Student? {
        val query =
                """
            SELECT student_id, enrollment_number, family_tutor_phone, user_id, tutor_id
            FROM students 
            WHERE user_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, userId)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return Student(
                    studentId = resultSet.getInt("student_id"),
                    enrollmentNumber = resultSet.getString("enrollment_number"),
                    familyTutorPhone = resultSet.getString("family_tutor_phone"),
                    userId = resultSet.getInt("user_id"),
                    tutorId = resultSet.getObject("tutor_id") as? Int
            )
        } catch (error: Exception) {
            throw Exception("Failed to get student by user id: ${error.message}")
        }
    }

    override suspend fun getByEnrollmentNumber(enrollmentNumber: String): Student? {
        val query =
                """
            SELECT student_id, enrollment_number, family_tutor_phone, user_id, tutor_id
            FROM students 
            WHERE enrollment_number = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setString(1, enrollmentNumber)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return Student(
                    studentId = resultSet.getInt("student_id"),
                    enrollmentNumber = resultSet.getString("enrollment_number"),
                    familyTutorPhone = resultSet.getString("family_tutor_phone"),
                    userId = resultSet.getInt("user_id"),
                    tutorId = resultSet.getObject("tutor_id") as? Int
            )
        } catch (error: Exception) {
            throw Exception("Failed to get student by enrollment number: ${error.message}")
        }
    }

    override suspend fun search(query: String): List<Map<String, Any?>> {
        val sql =
                """
        SELECT 
            s.student_id,
            s.enrollment_number,
            s.family_tutor_phone,
            s.user_id,
            s.tutor_id,
            CONCAT(
                COALESCE(u.first_name, ''), 
                ' ', 
                COALESCE(u.middle_name, ''), 
                ' ', 
                COALESCE(u.last_name, ''), 
                ' ', 
                COALESCE(u.second_last_name, '')
            ) AS full_name,
            u.email,
            u.phone,
            r.role_name,
            r.description AS role_description,
            u.registration_date
        FROM students s
        INNER JOIN users u ON s.user_id = u.user_id
        INNER JOIN roles r ON u.role_id = r.role_id
        WHERE s.enrollment_number LIKE ? 
           OR CONCAT(
                COALESCE(u.first_name, ''), 
                ' ', 
                COALESCE(u.middle_name, ''), 
                ' ', 
                COALESCE(u.last_name, ''), 
                ' ', 
                COALESCE(u.second_last_name, '')
              ) LIKE ?
        ORDER BY s.student_id DESC
    """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(sql)
            val searchPattern = "%$query%"
            statement.setString(1, searchPattern)
            statement.setString(2, searchPattern)

            val resultSet = statement.executeQuery()

            val students = mutableListOf<Map<String, Any?>>()

            while (resultSet.next()) {
                students.add(
                        mapOf(
                                "student_id" to resultSet.getInt("student_id"),
                                "enrollment_number" to resultSet.getString("enrollment_number"),
                                "family_tutor_phone" to resultSet.getString("family_tutor_phone"),
                                "user_id" to resultSet.getInt("user_id"),
                                "tutor_id" to resultSet.getObject("tutor_id"),
                                "full_name" to (resultSet.getString("full_name") ?: ""),
                                "email" to (resultSet.getString("email") ?: ""),
                                "phone" to resultSet.getString("phone"),
                                "role_name" to (resultSet.getString("role_name") ?: ""),
                                "role_description" to resultSet.getString("role_description"),
                                "registration_date" to
                                        resultSet
                                                .getTimestamp("registration_date")
                                                .toLocalDateTime()
                        )
                )
            }

            return students
        } catch (error: Exception) {
            throw Exception("Failed to search students: ${error.message}")
        }
    }

    override suspend fun update(student: Student) {
        val query =
                """
            UPDATE students 
            SET enrollment_number = ?, family_tutor_phone = ?, user_id = ?, tutor_id = ?
            WHERE student_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)

            statement.setString(1, student.enrollmentNumber)
            statement.setString(2, student.familyTutorPhone)
            statement.setInt(3, student.userId)
            if (student.tutorId != null) {
                statement.setInt(4, student.tutorId)
            } else {
                statement.setNull(4, java.sql.Types.INTEGER)
            }
            statement.setInt(5, student.studentId!!)

            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("Student not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to update student: ${error.message}")
        }
    }

    override suspend fun delete(studentId: Int) {
        val query = "DELETE FROM students WHERE student_id = ?"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, studentId)

            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("Student not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to delete student: ${error.message}")
        }
    }
}
