package tutors.infrastructure.adapters

import core.ConnMySQL
import tutors.domain.ITutorRepository
import tutors.domain.entities.Tutor
import tutors.domain.entities.TutorWithDetails

class MySQLTutorRepository(private val conn: ConnMySQL) : ITutorRepository {

    override suspend fun save(tutor: Tutor): Tutor {
        val query = """
            INSERT INTO tutors (user_id) 
            VALUES (?)
        """

        try {
            val connection = conn.getConnection()
            val statement =
                    connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)

            statement.setInt(1, tutor.userId)

            statement.executeUpdate()

            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) {
                val id = generatedKeys.getInt(1)
                return tutor.copy(tutorId = id)
            }

            throw Exception("Failed to get generated tutor ID")
        } catch (error: Exception) {
            throw Exception("Failed to save tutor: ${error.message}")
        }
    }

    override suspend fun getAll(): List<Tutor> {
        val query =
                """
            SELECT tutor_id, user_id 
            FROM tutors 
            ORDER BY tutor_id DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            val tutors = mutableListOf<Tutor>()

            while (resultSet.next()) {
                tutors.add(
                        Tutor(
                                tutorId = resultSet.getInt("tutor_id"),
                                userId = resultSet.getInt("user_id")
                        )
                )
            }

            return tutors
        } catch (error: Exception) {
            throw Exception("Failed to get all tutors: ${error.message}")
        }
    }

    override suspend fun getById(id: Int): Tutor? {
        val query =
                """
            SELECT tutor_id, user_id 
            FROM tutors 
            WHERE tutor_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, id)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return Tutor(
                    tutorId = resultSet.getInt("tutor_id"),
                    userId = resultSet.getInt("user_id")
            )
        } catch (error: Exception) {
            throw Exception("Failed to get tutor by id: ${error.message}")
        }
    }

    override suspend fun getByUserId(userId: Int): Tutor? {
        val query =
                """
            SELECT tutor_id, user_id 
            FROM tutors 
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

            return Tutor(
                    tutorId = resultSet.getInt("tutor_id"),
                    userId = resultSet.getInt("user_id")
            )
        } catch (error: Exception) {
            throw Exception("Failed to get tutor by user id: ${error.message}")
        }
    }

    override suspend fun update(tutor: Tutor) {
        val query =
                """
            UPDATE tutors 
            SET user_id = ? 
            WHERE tutor_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)

            statement.setInt(1, tutor.userId)
            statement.setInt(2, tutor.tutorId!!)

            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("Tutor not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to update tutor: ${error.message}")
        }
    }

    override suspend fun delete(id: Int) {
        val query = "DELETE FROM tutors WHERE tutor_id = ?"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, id)

            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("Tutor not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to delete tutor: ${error.message}")
        }
    }

    override suspend fun getAllWithDetails(): List<TutorWithDetails> {
        val query =
                """
        SELECT 
            t.tutor_id,
            t.user_id,
            u.first_name,
            u.middle_name,
            u.last_name,
            u.second_last_name,
            u.email,
            u.phone,
            r.role_name,
            r.description as role_description,
            u.registration_date
        FROM tutors t
        INNER JOIN users u ON t.user_id = u.user_id
        INNER JOIN roles r ON u.role_id = r.role_id
        ORDER BY t.tutor_id DESC
    """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            val tutors = mutableListOf<TutorWithDetails>()

            while (resultSet.next()) {
                tutors.add(
                        TutorWithDetails(
                                tutorId = resultSet.getInt("tutor_id"),
                                userId = resultSet.getInt("user_id"),
                                firstName = resultSet.getString("first_name"),
                                middleName = resultSet.getString("middle_name"),
                                lastName = resultSet.getString("last_name"),
                                secondLastName = resultSet.getString("second_last_name"),
                                email = resultSet.getString("email"),
                                phone = resultSet.getString("phone"),
                                roleName = resultSet.getString("role_name"),
                                roleDescription = resultSet.getString("role_description"),
                                registrationDate =
                                        resultSet
                                                .getTimestamp("registration_date")
                                                .toLocalDateTime()
                        )
                )
            }

            return tutors
        } catch (error: Exception) {
            throw Exception("Failed to get all tutors with details: ${error.message}")
        }
    }

    override suspend fun getByIdWithDetails(id: Int): TutorWithDetails? {
        val query =
                """
        SELECT 
            t.tutor_id,
            t.user_id,
            u.first_name,
            u.middle_name,
            u.last_name,
            u.second_last_name,
            u.email,
            u.phone,
            r.role_name,
            r.description as role_description,
            u.registration_date
        FROM tutors t
        INNER JOIN users u ON t.user_id = u.user_id
        INNER JOIN roles r ON u.role_id = r.role_id
        WHERE t.tutor_id = ?
    """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, id)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return TutorWithDetails(
                    tutorId = resultSet.getInt("tutor_id"),
                    userId = resultSet.getInt("user_id"),
                    firstName = resultSet.getString("first_name"),
                    middleName = resultSet.getString("middle_name"),
                    lastName = resultSet.getString("last_name"),
                    secondLastName = resultSet.getString("second_last_name"),
                    email = resultSet.getString("email"),
                    phone = resultSet.getString("phone"),
                    roleName = resultSet.getString("role_name"),
                    roleDescription = resultSet.getString("role_description"),
                    registrationDate = resultSet.getTimestamp("registration_date").toLocalDateTime()
            )
        } catch (error: Exception) {
            throw Exception("Failed to get tutor by id with details: ${error.message}")
        }
    }
}
