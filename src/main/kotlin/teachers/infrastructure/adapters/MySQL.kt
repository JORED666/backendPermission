package teachers.infrastructure.adapters

import teachers.domain.ITeacherRepository  
import teachers.domain.entities.Teacher
import teachers.domain.entities.TeacherWithDetails
import teachers.domain.entities.PersonalInfo
import teachers.domain.entities.RoleInfo
import core.ConnMySQL
import java.time.LocalDateTime

class MySQLTeacherRepository(private val conn: ConnMySQL) : ITeacherRepository {
    
    override suspend fun save(teacher: Teacher): Teacher {
        val query = """
            INSERT INTO teachers (user_id) 
            VALUES (?)
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
            
            statement.setInt(1, teacher.userId)
            
            statement.executeUpdate()
            
            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) {
                val id = generatedKeys.getInt(1)
                return teacher.copy(teacherId = id)
            }
            
            throw Exception("Failed to get generated teacher ID")
        } catch (error: Exception) {
            throw Exception("Failed to save teacher: ${error.message}")
        }
    }

    override suspend fun getById(teacherId: Int): Teacher? {
        val query = """
            SELECT teacher_id, user_id 
            FROM teachers 
            WHERE teacher_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, teacherId)
            
            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return Teacher(
                teacherId = resultSet.getInt("teacher_id"),
                userId = resultSet.getInt("user_id")
            )
        } catch (error: Exception) {
            throw Exception("Failed to get teacher by id: ${error.message}")
        }
    }

    override suspend fun getAll(): List<Teacher> {
        val query = """
            SELECT teacher_id, user_id 
            FROM teachers 
            ORDER BY teacher_id DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            
            val teachers = mutableListOf<Teacher>()
            
            while (resultSet.next()) {
                teachers.add(Teacher(
                    teacherId = resultSet.getInt("teacher_id"),
                    userId = resultSet.getInt("user_id")
                ))
            }
            
            return teachers
        } catch (error: Exception) {
            throw Exception("Failed to get all teachers: ${error.message}")
        }
    }

    override suspend fun getAllWithDetails(): List<TeacherWithDetails> {
        val query = """
            SELECT 
                t.teacher_id,
                t.user_id,
                CONCAT(u.first_name, ' ', COALESCE(u.middle_name, ''), ' ', u.last_name, ' ', COALESCE(u.second_last_name, '')) AS full_name,
                u.email,
                u.phone,
                r.role_name,
                r.description AS role_description,
                u.registration_date
            FROM teachers t
            INNER JOIN users u ON t.user_id = u.user_id
            INNER JOIN roles r ON u.role_id = r.role_id
            ORDER BY t.teacher_id DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            
            val teachers = mutableListOf<TeacherWithDetails>()
            
            while (resultSet.next()) {
                teachers.add(TeacherWithDetails(
                    teacherId = resultSet.getInt("teacher_id"),
                    userId = resultSet.getInt("user_id"),
                    personalInfo = PersonalInfo(
                        fullName = resultSet.getString("full_name").trim(),
                        email = resultSet.getString("email"),
                        phone = resultSet.getString("phone")
                    ),
                    roleInfo = RoleInfo(
                        roleName = resultSet.getString("role_name"),
                        description = resultSet.getString("role_description")
                    ),
                    registrationDate = resultSet.getTimestamp("registration_date").toLocalDateTime()
                ))
            }
            
            return teachers
        } catch (error: Exception) {
            throw Exception("Failed to get all teachers with details: ${error.message}")
        }
    }

    override suspend fun getByIdWithDetails(teacherId: Int): TeacherWithDetails? {
        val query = """
            SELECT 
                t.teacher_id,
                t.user_id,
                CONCAT(u.first_name, ' ', COALESCE(u.middle_name, ''), ' ', u.last_name, ' ', COALESCE(u.second_last_name, '')) AS full_name,
                u.email,
                u.phone,
                r.role_name,
                r.description AS role_description,
                u.registration_date
            FROM teachers t
            INNER JOIN users u ON t.user_id = u.user_id
            INNER JOIN roles r ON u.role_id = r.role_id
            WHERE t.teacher_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, teacherId)
            
            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return TeacherWithDetails(
                teacherId = resultSet.getInt("teacher_id"),
                userId = resultSet.getInt("user_id"),
                personalInfo = PersonalInfo(
                    fullName = resultSet.getString("full_name").trim(),
                    email = resultSet.getString("email"),
                    phone = resultSet.getString("phone")
                ),
                roleInfo = RoleInfo(
                    roleName = resultSet.getString("role_name"),
                    description = resultSet.getString("role_description")
                ),
                registrationDate = resultSet.getTimestamp("registration_date").toLocalDateTime()
            )
        } catch (error: Exception) {
            throw Exception("Failed to get teacher by id with details: ${error.message}")
        }
    }

    override suspend fun getByUserId(userId: Int): Teacher? {
        val query = """
            SELECT teacher_id, user_id 
            FROM teachers 
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

            return Teacher(
                teacherId = resultSet.getInt("teacher_id"),
                userId = resultSet.getInt("user_id")
            )
        } catch (error: Exception) {
            throw Exception("Failed to get teacher by user id: ${error.message}")
        }
    }

    override suspend fun update(teacher: Teacher) {
        val query = """
            UPDATE teachers 
            SET user_id = ? 
            WHERE teacher_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            
            statement.setInt(1, teacher.userId)
            statement.setInt(2, teacher.teacherId!!)
            
            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("Teacher not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to update teacher: ${error.message}")
        }
    }

    override suspend fun delete(teacherId: Int) {
        val query = "DELETE FROM teachers WHERE teacher_id = ?"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, teacherId)
            
            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("Teacher not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to delete teacher: ${error.message}")
        }
    }
}