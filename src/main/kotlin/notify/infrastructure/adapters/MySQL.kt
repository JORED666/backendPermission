package notify.infrastructure.adapters

import core.ConnMySQL
import notify.domain.INotifyRepository
import notify.domain.entities.Notify

class MySQLNotifyRepository(private val conn: ConnMySQL) : INotifyRepository {

    override suspend fun save(notify: Notify): Notify {
        val query =
                """
            INSERT INTO notifications (sender_id, receiver_id, type, message, related_permit_id, is_read) 
            VALUES (?, ?, ?, ?, ?, ?)
        """

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS).use {
                        statement ->
                    statement.setInt(1, notify.senderId)
                    statement.setInt(2, notify.receiverId)
                    statement.setString(3, notify.type)
                    statement.setString(4, notify.message)

                    if (notify.relatedPermitId != null) {
                        statement.setInt(5, notify.relatedPermitId)
                    } else {
                        statement.setNull(5, java.sql.Types.INTEGER)
                    }

                    statement.setBoolean(6, notify.isRead)
                    statement.executeUpdate()

                    statement.generatedKeys.use { generatedKeys ->
                        if (generatedKeys.next()) {
                            val id = generatedKeys.getInt(1)
                            return notify.copy(notificationId = id)
                        }
                        throw Exception("Failed to get generated notification ID")
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to save notification: ${error.message}")
        }
    }

    override suspend fun getById(notificationId: Int): Notify? {
        val query =
                """
            SELECT notification_id, sender_id, receiver_id, type, message, 
                   related_permit_id, is_read, created_at
            FROM notifications 
            WHERE notification_id = ?
        """

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, notificationId)

                    statement.executeQuery().use { resultSet ->
                        if (!resultSet.next()) {
                            return null
                        }

                        return Notify(
                                notificationId = resultSet.getInt("notification_id"),
                                senderId = resultSet.getInt("sender_id"),
                                receiverId = resultSet.getInt("receiver_id"),
                                type = resultSet.getString("type"),
                                message = resultSet.getString("message"),
                                relatedPermitId = resultSet.getObject("related_permit_id") as? Int,
                                isRead = resultSet.getBoolean("is_read"),
                                createdAt = resultSet.getTimestamp("created_at")?.toLocalDateTime()
                        )
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get notification by id: ${error.message}")
        }
    }

    override suspend fun getByReceiverId(receiverId: Int): List<Notify> {
        val query =
                """
            SELECT notification_id, sender_id, receiver_id, type, message, 
                   related_permit_id, is_read, created_at
            FROM notifications 
            WHERE receiver_id = ?
            ORDER BY created_at DESC
        """

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, receiverId)

                    statement.executeQuery().use { resultSet ->
                        val notifications = mutableListOf<Notify>()

                        while (resultSet.next()) {
                            notifications.add(
                                    Notify(
                                            notificationId = resultSet.getInt("notification_id"),
                                            senderId = resultSet.getInt("sender_id"),
                                            receiverId = resultSet.getInt("receiver_id"),
                                            type = resultSet.getString("type"),
                                            message = resultSet.getString("message"),
                                            relatedPermitId =
                                                    resultSet.getObject("related_permit_id") as?
                                                            Int,
                                            isRead = resultSet.getBoolean("is_read"),
                                            createdAt =
                                                    resultSet
                                                            .getTimestamp("created_at")
                                                            ?.toLocalDateTime()
                                    )
                            )
                        }

                        return notifications
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get notifications by receiver id: ${error.message}")
        }
    }

    override suspend fun getUnreadByReceiverId(receiverId: Int): List<Notify> {
        val query =
                """
            SELECT notification_id, sender_id, receiver_id, type, message, 
                   related_permit_id, is_read, created_at
            FROM notifications 
            WHERE receiver_id = ? AND is_read = FALSE
            ORDER BY created_at DESC
        """

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, receiverId)

                    statement.executeQuery().use { resultSet ->
                        val notifications = mutableListOf<Notify>()

                        while (resultSet.next()) {
                            notifications.add(
                                    Notify(
                                            notificationId = resultSet.getInt("notification_id"),
                                            senderId = resultSet.getInt("sender_id"),
                                            receiverId = resultSet.getInt("receiver_id"),
                                            type = resultSet.getString("type"),
                                            message = resultSet.getString("message"),
                                            relatedPermitId =
                                                    resultSet.getObject("related_permit_id") as?
                                                            Int,
                                            isRead = resultSet.getBoolean("is_read"),
                                            createdAt =
                                                    resultSet
                                                            .getTimestamp("created_at")
                                                            ?.toLocalDateTime()
                                    )
                            )
                        }

                        return notifications
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get unread notifications: ${error.message}")
        }
    }

    override suspend fun markAsRead(notificationId: Int) {
        val query =
                """
            UPDATE notifications 
            SET is_read = TRUE 
            WHERE notification_id = ?
        """

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, notificationId)

                    val rowsAffected = statement.executeUpdate()

                    if (rowsAffected == 0) {
                        throw Exception("Notification not found")
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to mark notification as read: ${error.message}")
        }
    }

    override suspend fun markAllAsReadByReceiver(receiverId: Int) {
        val query =
                """
            UPDATE notifications 
            SET is_read = TRUE 
            WHERE receiver_id = ? AND is_read = FALSE
        """

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, receiverId)
                    statement.executeUpdate()
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to mark all notifications as read: ${error.message}")
        }
    }

    override suspend fun getWithDetails(receiverId: Int): List<Map<String, Any?>> {
        val query =
                """
        SELECT 
            n.notification_id,
            n.sender_id,
            n.receiver_id,
            n.type,
            n.message,
            n.related_permit_id,
            n.is_read,
            n.created_at,
            TRIM(CONCAT(
                u.first_name, ' ', 
                COALESCE(u.middle_name, ''), ' ', 
                u.last_name, ' ', 
                COALESCE(u.second_last_name, '')
            )) AS sender_name,
            u.email AS sender_email,
            p.permit_id,
            s.enrollment_number AS student_matricula,
            p.reason AS permit_reason,
            p.status AS permit_status
        FROM notifications n
        INNER JOIN users u ON n.sender_id = u.user_id
        LEFT JOIN permits p ON n.related_permit_id = p.permit_id
        LEFT JOIN students s ON p.student_id = s.student_id
        WHERE n.receiver_id = ?
        ORDER BY n.created_at DESC
    """

        try {
            conn.getConnection().use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setInt(1, receiverId)

                    statement.executeQuery().use { resultSet ->
                        val notifications = mutableListOf<Map<String, Any?>>()

                        while (resultSet.next()) {
                            notifications.add(
                                    mapOf(
                                            "notification_id" to
                                                    resultSet.getInt("notification_id"),
                                            "sender_id" to resultSet.getInt("sender_id"),
                                            "receiver_id" to resultSet.getInt("receiver_id"),
                                            "type" to resultSet.getString("type"),
                                            "message" to resultSet.getString("message"),
                                            "related_permit_id" to
                                                    resultSet.getObject("related_permit_id"),
                                            "is_read" to resultSet.getBoolean("is_read"),
                                            "created_at" to
                                                    resultSet
                                                            .getTimestamp("created_at")
                                                            ?.toLocalDateTime(),
                                            "sender_name" to resultSet.getString("sender_name"),
                                            "sender_email" to resultSet.getString("sender_email"),
                                            "permit_id" to resultSet.getObject("permit_id"),
                                            "student_matricula" to
                                                    resultSet.getString("student_matricula"),
                                            "permit_reason" to resultSet.getString("permit_reason"),
                                            "permit_status" to resultSet.getString("permit_status")
                                    )
                            )
                        }

                        return notifications
                    }
                }
            }
        } catch (error: Exception) {
            throw Exception("Failed to get notifications with details: ${error.message}")
        }
    }
}
