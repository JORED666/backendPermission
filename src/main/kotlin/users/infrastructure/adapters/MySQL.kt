package users.infrastructure.adapters

import core.ConnMySQL
import java.sql.Timestamp
import users.domain.IUserRepository
import users.domain.entities.User

class MySQLUserRepository(private val conn: ConnMySQL) : IUserRepository {

    override suspend fun save(user: User): User {
        val query =
                """
            INSERT INTO users (first_name, middle_name, last_name, second_last_name, email, phone, password, registration_date, role_id, oauth_provider, oauth_id) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        try {
            val connection = conn.getConnection()
            val statement =
                    connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)

            statement.setString(1, user.firstName)
            statement.setString(2, user.middleName)
            statement.setString(3, user.lastName)
            statement.setString(4, user.secondLastName)
            statement.setString(5, user.email)
            statement.setString(6, user.phone)
            statement.setString(7, user.password)
            statement.setTimestamp(8, Timestamp.valueOf(user.registrationDate))
            statement.setInt(9, user.roleId)
            statement.setString(10, user.oauthProvider)
            statement.setString(11, user.oauthId)

            statement.executeUpdate()

            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) {
                val id = generatedKeys.getInt(1)
                return user.copy(userId = id)
            }

            throw Exception("Failed to get generated user ID")
        } catch (error: Exception) {
            throw Exception("Failed to save user: ${error.message}")
        }
    }

    override suspend fun getByEmail(email: String): User? {
        val query =
                """
            SELECT user_id, first_name, middle_name, last_name, second_last_name, email, phone, password, registration_date, role_id, oauth_provider, oauth_id
            FROM users 
            WHERE email = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setString(1, email)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return User(
                    userId = resultSet.getInt("user_id"),
                    firstName = resultSet.getString("first_name"),
                    middleName = resultSet.getString("middle_name"),
                    lastName = resultSet.getString("last_name"),
                    secondLastName = resultSet.getString("second_last_name"),
                    email = resultSet.getString("email"),
                    phone = resultSet.getString("phone"),
                    password = resultSet.getString("password") ?: "",
                    registrationDate =
                            resultSet.getTimestamp("registration_date").toLocalDateTime(),
                    roleId = resultSet.getInt("role_id"),
                    oauthProvider = resultSet.getString("oauth_provider"),
                    oauthId = resultSet.getString("oauth_id")
            )
        } catch (error: Exception) {
            throw Exception("Failed to get user by email: ${error.message}")
        }
    }

    // MÉTODO NUEVO PARA OAUTH
    override suspend fun getByOAuthId(provider: String, oauthId: String): User? {
        val query =
                """
            SELECT user_id, first_name, middle_name, last_name, second_last_name, email, phone, password, registration_date, role_id, oauth_provider, oauth_id
            FROM users 
            WHERE oauth_provider = ? AND oauth_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setString(1, provider)
            statement.setString(2, oauthId)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return User(
                    userId = resultSet.getInt("user_id"),
                    firstName = resultSet.getString("first_name"),
                    middleName = resultSet.getString("middle_name"),
                    lastName = resultSet.getString("last_name"),
                    secondLastName = resultSet.getString("second_last_name"),
                    email = resultSet.getString("email"),
                    phone = resultSet.getString("phone"),
                    password = resultSet.getString("password") ?: "",
                    registrationDate =
                            resultSet.getTimestamp("registration_date").toLocalDateTime(),
                    roleId = resultSet.getInt("role_id"),
                    oauthProvider = resultSet.getString("oauth_provider"),
                    oauthId = resultSet.getString("oauth_id")
            )
        } catch (error: Exception) {
            throw Exception("Failed to get user by OAuth ID: ${error.message}")
        }
    }

    override suspend fun getAll(): List<User> {
        val query =
                """
            SELECT user_id, first_name, middle_name, last_name, second_last_name, email, phone, password, registration_date, role_id, oauth_provider, oauth_id
            FROM users 
            ORDER BY registration_date DESC
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            val users = mutableListOf<User>()

            while (resultSet.next()) {
                users.add(
                        User(
                                userId = resultSet.getInt("user_id"),
                                firstName = resultSet.getString("first_name"),
                                middleName = resultSet.getString("middle_name"),
                                lastName = resultSet.getString("last_name"),
                                secondLastName = resultSet.getString("second_last_name"),
                                email = resultSet.getString("email"),
                                phone = resultSet.getString("phone"),
                                password = resultSet.getString("password") ?: "",
                                registrationDate =
                                        resultSet
                                                .getTimestamp("registration_date")
                                                .toLocalDateTime(),
                                roleId = resultSet.getInt("role_id"),
                                oauthProvider = resultSet.getString("oauth_provider"),
                                oauthId = resultSet.getString("oauth_id")
                        )
                )
            }

            return users
        } catch (error: Exception) {
            throw Exception("Failed to get all users: ${error.message}")
        }
    }

    override suspend fun getById(id: Int): User? {
        val query =
                """
            SELECT user_id, first_name, middle_name, last_name, second_last_name, email, phone, password, registration_date, role_id, oauth_provider, oauth_id
            FROM users 
            WHERE user_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, id)

            val resultSet = statement.executeQuery()

            if (!resultSet.next()) {
                return null
            }

            return User(
                    userId = resultSet.getInt("user_id"),
                    firstName = resultSet.getString("first_name"),
                    middleName = resultSet.getString("middle_name"),
                    lastName = resultSet.getString("last_name"),
                    secondLastName = resultSet.getString("second_last_name"),
                    email = resultSet.getString("email"),
                    phone = resultSet.getString("phone"),
                    password = resultSet.getString("password") ?: "",
                    registrationDate =
                            resultSet.getTimestamp("registration_date").toLocalDateTime(),
                    roleId = resultSet.getInt("role_id"),
                    oauthProvider = resultSet.getString("oauth_provider"),
                    oauthId = resultSet.getString("oauth_id")
            )
        } catch (error: Exception) {
            throw Exception("Failed to get user by id: ${error.message}")
        }
    }

    override suspend fun update(user: User) {
        val query =
                """
            UPDATE users 
            SET first_name = ?, 
                middle_name = ?, 
                last_name = ?, 
                second_last_name = ?, 
                email = ?, 
                phone = ?, 
                role_id = ?,
                oauth_provider = ?,
                oauth_id = ?
            WHERE user_id = ?
        """

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)

            statement.setString(1, user.firstName)
            statement.setString(2, user.middleName)
            statement.setString(3, user.lastName)
            statement.setString(4, user.secondLastName)
            statement.setString(5, user.email)
            statement.setString(6, user.phone)
            statement.setInt(7, user.roleId)
            statement.setString(8, user.oauthProvider)
            statement.setString(9, user.oauthId)
            statement.setInt(10, user.userId!!)

            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("User not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to update user: ${error.message}")
        }
    }

    override suspend fun delete(id: Int) {
        val query = "DELETE FROM users WHERE user_id = ?"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, id)

            val rowsAffected = statement.executeUpdate()

            if (rowsAffected == 0) {
                throw Exception("User not found")
            }
        } catch (error: Exception) {
            throw Exception("Failed to delete user: ${error.message}")
        }
    }

    override suspend fun insertTeacher(userId: Int) {
        val query = "INSERT INTO teachers (user_id) VALUES (?)"

        try {
            val connection = conn.getConnection()
            val statement = connection.prepareStatement(query)
            statement.setInt(1, userId)
            statement.executeUpdate()
            println("Usuario $userId insertado en tabla teachers")
        } catch (error: Exception) {
            if (error.message?.contains("Duplicate entry") != true) {
                throw Exception("Failed to insert teacher: ${error.message}")
            } else {
                println("Usuario $userId ya existía en teachers")
            }
        }
    }
}
