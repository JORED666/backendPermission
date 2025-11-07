package core

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.delay
import java.sql.Connection

class ConnMySQL {
    private val dataSource: HikariDataSource

    init {
        val dotenv = dotenv {
            ignoreIfMissing = true
        }
        
        val dbHost = dotenv["DB_HOST"] ?: System.getenv("DB_HOST")
        val dbUser = dotenv["DB_USER"] ?: System.getenv("DB_USER")
        val dbPassword = dotenv["DB_PASSWORD"] ?: System.getenv("DB_PASSWORD")
        val dbName = dotenv["DB_NAME"] ?: System.getenv("DB_NAME")
        val dbPort = dotenv["DB_PORT"] ?: System.getenv("DB_PORT") ?: "3306"
        val dbSsl = (dotenv["DB_SSL"] ?: System.getenv("DB_SSL")) == "true"

        if (dbHost == null || dbUser == null || dbPassword == null || dbName == null) {
            throw Exception("Error: Faltan variables de entorno. Verifica tu .env")
        }

        val config = HikariConfig().apply {
            jdbcUrl = if (dbSsl) {
                "jdbc:mysql://$dbHost:$dbPort/$dbName?useSSL=true&requireSSL=false&verifyServerCertificate=false"
            } else {
                "jdbc:mysql://$dbHost:$dbPort/$dbName?useSSL=false"
            }
            username = dbUser
            password = dbPassword
            driverClassName = "com.mysql.cj.jdbc.Driver"
            
            maximumPoolSize = 10
            minimumIdle = 5
            connectionTimeout = 10000
            idleTimeout = 600000
            maxLifetime = 1800000
            keepaliveTime = 30000
            
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        }

        dataSource = HikariDataSource(config)
        
        println("Pool de conexiones creado")
        testConnection()
    }

    private fun testConnection() {
        try {
            dataSource.connection.use { connection ->
                connection.prepareStatement("SELECT NOW()").use { statement ->
                    statement.executeQuery().use { 
                    }
                }
            }
            println("Conexión a MySQL exitosa.")
        } catch (error: Exception) {
            println("Error al verificar la conexión a la base de datos: ${error.message}")
        }
    }

    suspend fun query(text: String, params: List<Any?>? = null): QueryResult {
        val maxRetries = 3
        var lastError: Exception? = null

        for (i in 0 until maxRetries) {
            try {
                return dataSource.connection.use { connection ->
                    connection.prepareStatement(text).use { statement ->
                        params?.forEachIndexed { index, param ->
                            statement.setObject(index + 1, param)
                        }
                        
                        statement.executeQuery().use { resultSet ->
                            val rows = mutableListOf<Map<String, Any?>>()
                            val metadata = resultSet.metaData
                            val columnCount = metadata.columnCount
                            
                            while (resultSet.next()) {
                                val row = mutableMapOf<String, Any?>()
                                for (j in 1..columnCount) {
                                    row[metadata.getColumnName(j)] = resultSet.getObject(j)
                                }
                                rows.add(row)
                            }
                            
                            QueryResult(rows)
                        }
                    }
                }
            } catch (error: Exception) {
                lastError = error
                println("Error en query (intento ${i + 1}/$maxRetries): ${error.message}")
                
                if (error.message?.contains("ECONNRESET") == true || 
                    error.message?.contains("PROTOCOL_CONNECTION_LOST") == true) {
                    if (i < maxRetries - 1) {
                        delay(1000L * (i + 1))
                        continue
                    }
                }
                throw error
            }
        }
        
        throw lastError!!
    }

    fun getConnection(): Connection {
        return dataSource.connection
    }

    fun close() {
        dataSource.close()
    }
}

data class QueryResult(
    val rows: List<Map<String, Any?>>
)

fun getDBPool(): ConnMySQL {
    return ConnMySQL()
}