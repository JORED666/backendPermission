package permitsTeacher.infrastructure.adapters

import permitsTeacher.domain.IPermitTeacherRepository
import permitsTeacher.domain.entities.PermitTeacher
import core.ConnMySQL

class MySQLPermitTeacherRepository(private val conn: ConnMySQL) : IPermitTeacherRepository {
    
    override suspend fun save(permitTeacher: PermitTeacher): PermitTeacher {
        val query = """
            INSERT INTO permits_teachers (permit_id, teacher_id) 
            VALUES (?, ?)
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            
            statement.setInt(1, permitTeacher.permitId)
            statement.setInt(2, permitTeacher.teacherId)
            
            statement.executeUpdate()
            
            return permitTeacher
        } catch (error: Exception) {
            throw Exception("Failed to save permit teacher: ${error.message}")
        }
    }

    override suspend fun getByPermitAndTeacher(permitId: Int, teacherId: Int): PermitTeacher? {
        val query = """
            SELECT permit_id, teacher_id
            FROM permits_teachers 
            WHERE permit_id = ? AND teacher_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, permitId)
            statement.setInt(2, teacherId)
            
            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return PermitTeacher(
                permitId = resultSet.getInt("permit_id"),
                teacherId = resultSet.getInt("teacher_id")
            )
        } catch (error: Exception) {
            throw Exception("Failed to get permit teacher: ${error.message}")
        }
    }

    override suspend fun getAll(): List<PermitTeacher> {
        val query = """
            SELECT permit_id, teacher_id
            FROM permits_teachers 
            ORDER BY permit_id, teacher_id
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            
            val permitsTeachers = mutableListOf<PermitTeacher>()
            
            while (resultSet.next()) {
                permitsTeachers.add(PermitTeacher(
                    permitId = resultSet.getInt("permit_id"),
                    teacherId = resultSet.getInt("teacher_id")
                ))
            }
            
            return permitsTeachers
        } catch (error: Exception) {
            throw Exception("Failed to get all permits teachers: ${error.message}")
        }
    }

    override suspend fun getAllWithDetails(): List<Map<String, Any?>> {
        val query = """
            SELECT 
                pt.permit_id,
                pt.teacher_id,
                p.start_date,
                p.end_date,
                p.reason,
                p.status,
                p.cuatrimestre,
                CONCAT(us.first_name, ' ', COALESCE(us.middle_name, ''), ' ', us.last_name, ' ', COALESCE(us.second_last_name, '')) AS student_name,
                s.enrollment_number,
                CONCAT(ut.first_name, ' ', COALESCE(ut.middle_name, ''), ' ', ut.last_name, ' ', COALESCE(ut.second_last_name, '')) AS teacher_name,
                ut.email AS teacher_email
            FROM permits_teachers pt
            INNER JOIN permits p ON pt.permit_id = p.permit_id
            INNER JOIN students s ON p.student_id = s.student_id
            INNER JOIN users us ON s.user_id = us.user_id
            INNER JOIN teachers t ON pt.teacher_id = t.teacher_id
            INNER JOIN users ut ON t.user_id = ut.user_id
            ORDER BY pt.permit_id, pt.teacher_id
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()
            
            val permitsTeachers = mutableListOf<Map<String, Any?>>()
            
            while (resultSet.next()) {
                permitsTeachers.add(mapOf(
                    "permit_id" to resultSet.getInt("permit_id"),
                    "teacher_id" to resultSet.getInt("teacher_id"),
                    "start_date" to resultSet.getDate("start_date"),
                    "end_date" to resultSet.getDate("end_date"),
                    "reason" to resultSet.getString("reason"),
                    "status" to resultSet.getString("status"),
                    "cuatrimestre" to resultSet.getInt("cuatrimestre"),
                    "student_name" to resultSet.getString("student_name"),
                    "enrollment_number" to resultSet.getString("enrollment_number"),
                    "teacher_name" to resultSet.getString("teacher_name"),
                    "teacher_email" to resultSet.getString("teacher_email")
                ))
            }
            
            return permitsTeachers
        } catch (error: Exception) {
            throw Exception("Failed to get all permits teachers with details: ${error.message}")
        }
    }

    override suspend fun getByPermitId(permitId: Int): List<PermitTeacher> {
        val query = """
            SELECT permit_id, teacher_id
            FROM permits_teachers 
            WHERE permit_id = ?
            ORDER BY teacher_id
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, permitId)
            
            val resultSet = statement.executeQuery()
            
            val permitsTeachers = mutableListOf<PermitTeacher>()
            
            while (resultSet.next()) {
                permitsTeachers.add(PermitTeacher(
                    permitId = resultSet.getInt("permit_id"),
                    teacherId = resultSet.getInt("teacher_id")
                ))
            }
            
            return permitsTeachers
        } catch (error: Exception) {
            throw Exception("Failed to get permits teachers by permit id: ${error.message}")
        }
    }

    override suspend fun getByTeacherId(teacherId: Int): List<PermitTeacher> {
        val query = """
            SELECT permit_id, teacher_id
            FROM permits_teachers 
            WHERE teacher_id = ?
            ORDER BY permit_id
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, teacherId)
            
            val resultSet = statement.executeQuery()
            
            val permitsTeachers = mutableListOf<PermitTeacher>()
            
            while (resultSet.next()) {
                permitsTeachers.add(PermitTeacher(
                    permitId = resultSet.getInt("permit_id"),
                    teacherId = resultSet.getInt("teacher_id")
                ))
            }
            
            return permitsTeachers
        } catch (error: Exception) {
            throw Exception("Failed to get permits teachers by teacher id: ${error.message}")
        }
    }

    override suspend fun getByPermitIdWithDetails(permitId: Int): List<Map<String, Any?>> {
        val query = """
            SELECT 
                pt.permit_id,
                pt.teacher_id,
                p.start_date,
                p.end_date,
                p.reason,
                p.status,
                p.cuatrimestre,
                CONCAT(us.first_name, ' ', COALESCE(us.middle_name, ''), ' ', us.last_name, ' ', COALESCE(us.second_last_name, '')) AS student_name,
                s.enrollment_number,
                CONCAT(ut.first_name, ' ', COALESCE(ut.middle_name, ''), ' ', ut.last_name, ' ', COALESCE(ut.second_last_name, '')) AS teacher_name,
                ut.email AS teacher_email
            FROM permits_teachers pt
            INNER JOIN permits p ON pt.permit_id = p.permit_id
            INNER JOIN students s ON p.student_id = s.student_id
            INNER JOIN users us ON s.user_id = us.user_id
            INNER JOIN teachers t ON pt.teacher_id = t.teacher_id
            INNER JOIN users ut ON t.user_id = ut.user_id
            WHERE pt.permit_id = ?
            ORDER BY pt.teacher_id
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, permitId)
            
            val resultSet = statement.executeQuery()
            
            val permitsTeachers = mutableListOf<Map<String, Any?>>()
            
            while (resultSet.next()) {
                permitsTeachers.add(mapOf(
                    "permit_id" to resultSet.getInt("permit_id"),
                    "teacher_id" to resultSet.getInt("teacher_id"),
                    "start_date" to resultSet.getDate("start_date"),
                    "end_date" to resultSet.getDate("end_date"),
                    "reason" to resultSet.getString("reason"),
                    "status" to resultSet.getString("status"),
                    "cuatrimestre" to resultSet.getInt("cuatrimestre"),
                    "student_name" to resultSet.getString("student_name"),
                    "enrollment_number" to resultSet.getString("enrollment_number"),
                    "teacher_name" to resultSet.getString("teacher_name"),
                    "teacher_email" to resultSet.getString("teacher_email")
                ))
            }
            
            return permitsTeachers
        } catch (error: Exception) {
            throw Exception("Failed to get permits teachers by permit id with details: ${error.message}")
        }
    }

    override suspend fun getByTeacherIdWithDetails(teacherId: Int): List<Map<String, Any?>> {
        val query = """
            SELECT 
                pt.permit_id,
                pt.teacher_id,
                p.start_date,
                p.end_date,
                p.reason,
                p.status,
                p.cuatrimestre,
                CONCAT(us.first_name, ' ', COALESCE(us.middle_name, ''), ' ', us.last_name, ' ', COALESCE(us.second_last_name, '')) AS student_name,
                s.enrollment_number,
                CONCAT(ut.first_name, ' ', COALESCE(ut.middle_name, ''), ' ', ut.last_name, ' ', COALESCE(ut.second_last_name, '')) AS teacher_name,
                ut.email AS teacher_email
            FROM permits_teachers pt
            INNER JOIN permits p ON pt.permit_id = p.permit_id
            INNER JOIN students s ON p.student_id = s.student_id
            INNER JOIN users us ON s.user_id = us.user_id
            INNER JOIN teachers t ON pt.teacher_id = t.teacher_id
            INNER JOIN users ut ON t.user_id = ut.user_id
            WHERE pt.teacher_id = ?
            ORDER BY pt.permit_id
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, teacherId)
            
            val resultSet = statement.executeQuery()
            
            val permitsTeachers = mutableListOf<Map<String, Any?>>()
            
            while (resultSet.next()) {
                permitsTeachers.add(mapOf(
                    "permit_id" to resultSet.getInt("permit_id"),
                    "teacher_id" to resultSet.getInt("teacher_id"),
                    "start_date" to resultSet.getDate("start_date"),
                    "end_date" to resultSet.getDate("end_date"),
                    "reason" to resultSet.getString("reason"),
                    "status" to resultSet.getString("status"),
                    "cuatrimestre" to resultSet.getInt("cuatrimestre"),
                    "student_name" to resultSet.getString("student_name"),
                    "enrollment_number" to resultSet.getString("enrollment_number"),
                    "teacher_name" to resultSet.getString("teacher_name"),
                    "teacher_email" to resultSet.getString("teacher_email")
                ))
            }
            
            return permitsTeachers
        } catch (error: Exception) {
            throw Exception("Failed to get permits teachers by teacher id with details: ${error.message}")
        }
    }

    override suspend fun delete(permitId: Int, teacherId: Int) {
        val query = "DELETE FROM permits_teachers WHERE permit_id = ? AND teacher_id = ?"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, permitId)
            statement.setInt(2, teacherId)
            
            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("Permit teacher not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to delete permit teacher: ${error.message}")
        }
    }

    override suspend fun deleteByPermitId(permitId: Int) {
        val query = "DELETE FROM permits_teachers WHERE permit_id = ?"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, permitId)
            
            statement.executeUpdate()
        } catch (error: Exception) {
            throw Exception("Failed to delete permits teachers by permit id: ${error.message}")
        }
    }

    override suspend fun deleteByTeacherId(teacherId: Int) {
        val query = "DELETE FROM permits_teachers WHERE teacher_id = ?"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, teacherId)
            
            statement.executeUpdate()
        } catch (error: Exception) {
            throw Exception("Failed to delete permits teachers by teacher id: ${error.message}")
        }
    }
}