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
            
            // Configuración del Pool - OPTIMIZADA
            maximumPoolSize = 30              
            minimumIdle = 10                 
            connectionTimeout = 30000         
            idleTimeout = 300000              
            maxLifetime = 580000              // ✅ 9.6 min - Renovar ANTES que MySQL cierre (10 min)
            keepaliveTime = 300000            // ✅ 5 min - Hacer "ping" para mantener viva
            
            leakDetectionThreshold = 60000
            
            // Optimizaciones de rendimiento
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            addDataSourceProperty("useServerPrepStmts", "true")
            addDataSourceProperty("rewriteBatchedStatements", "true")
            addDataSourceProperty("cacheResultSetMetadata", "true")
            addDataSourceProperty("cacheServerConfiguration", "true")
            addDataSourceProperty("maintainTimeStats", "false")
            
            // Timeouts de red
            addDataSourceProperty("connectTimeout", "30000")
            addDataSourceProperty("socketTimeout", "30000")
            
            // Propiedades adicionales para evitar conexiones cerradas
            addDataSourceProperty("autoReconnect", "true")
            addDataSourceProperty("failOverReadOnly", "false")
            addDataSourceProperty("maxReconnects", "3")
            
            poolName = "PermitsHikariPool"
            
            // Habilitar validación de conexiones
            connectionTestQuery = "SELECT 1"
            validationTimeout = 3000
        }

        dataSource = HikariDataSource(config)
        
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("✅ HikariCP Pool inicializado:")
        println("   📊 Conexiones máximas: ${config.maximumPoolSize}")
        println("   📊 Conexiones mínimas idle: ${config.minimumIdle}")
        println("   ⏱️  Timeout de conexión: ${config.connectionTimeout}ms")
        println("   ⏱️  Max lifetime: ${config.maxLifetime}ms (${config.maxLifetime / 60000} min)")
        println("   💓 Keepalive: ${config.keepaliveTime}ms")
        println("   🔍 Detección de leaks: ${config.leakDetectionThreshold}ms")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        testConnection()
    }

    private fun testConnection() {
        try {
            dataSource.connection.use { connection ->
                connection.prepareStatement("SELECT NOW(), @@wait_timeout as wait_timeout").use { statement ->
                    statement.executeQuery().use { rs ->
                        if (rs.next()) {
                            val waitTimeout = rs.getInt("wait_timeout")
                            println("✅ Conexión a MySQL exitosa.")
                            println("   ⏱️  MySQL wait_timeout: ${waitTimeout}s (${waitTimeout / 60} min)")
                            
                            // Advertencia si maxLifetime es muy cercano a wait_timeout
                            val maxLifetimeSeconds = 580000 / 1000
                            if (maxLifetimeSeconds >= waitTimeout - 60) {
                                println("   ⚠️  ADVERTENCIA: maxLifetime está muy cerca del wait_timeout de MySQL")
                                println("      Se recomienda que maxLifetime sea al menos 60s menor que wait_timeout")
                            }
                        }
                    }
                }
            }
        } catch (error: Exception) {
            println("❌ Error al verificar la conexión a la base de datos: ${error.message}")
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
                println("⚠️ Error en query (intento ${i + 1}/$maxRetries): ${error.message}")
                
                if (error.message?.contains("ECONNRESET") == true || 
                    error.message?.contains("PROTOCOL_CONNECTION_LOST") == true ||
                    error.message?.contains("Connection is not available") == true ||
                    error.message?.contains("No operations allowed after connection closed") == true) {
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
        println("✅ Pool de conexiones cerrado")
    }
    
    fun printPoolStats() {
        try {
            val pool = dataSource.hikariPoolMXBean
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            println("📊 Estadísticas del Pool de Conexiones:")
            println("   🟢 Conexiones activas: ${pool.activeConnections}")
            println("   🔵 Conexiones idle: ${pool.idleConnections}")
            println("   📈 Total de conexiones: ${pool.totalConnections}")
            println("   ⏳ Threads esperando: ${pool.threadsAwaitingConnection}")
            println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        } catch (e: Exception) {
            println("❌ Error obteniendo estadísticas del pool: ${e.message}")
        }
    }
}

data class QueryResult(
    val rows: List<Map<String, Any?>>
)

fun getDBPool(): ConnMySQL {
    return ConnMySQL()
}