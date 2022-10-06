package phss.factions.database.impl

import phss.factions.config.providers.DatabaseConfig
import phss.factions.database.Database
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class MySQLDatabaseImpl(
    host: String,
    port: Int,
    database: String,
    private val user: String, private val password: String
) : Database {

    private val url: String = "jdbc:mysql://$host:$port/$database?autoReconnect=true"
    override val type = "MySQL"

    override var connection: Connection? = null
    override var statement: Statement? = null

    override fun open(): Connection? {
        if (isConnected) return connection

        Class.forName("com.mysql.cj.jdbc.Driver").newInstance()
        if (connection == null) connection = DriverManager.getConnection(url, user, password)
        if (statement == null && connection != null) statement = connection!!.createStatement()

        return connection
    }
    override fun close(): Boolean {
        statement?.close()
        connection?.close()
        statement = null
        connection = null

        return isConnected
    }

    override val isConnected: Boolean
        get() = connection != null

    constructor(config: DatabaseConfig.Database) : this(
        config.hostname, config.port,
        config.database,
        config.username, config.password
    )

}