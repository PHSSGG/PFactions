package phss.factions.database.impl

import phss.factions.database.Database
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class SQLiteDatabaseImpl(
    private val path: String
) : Database {

    override var connection: Connection? = null
    override var statement: Statement? = null

    override val type = "SQLite"

    override fun open(): Connection? {
        if (isConnected) return connection

        File(path).run {
            if (!exists()) mkdirs()
        }
        Class.forName("org.sqlite.JDBC")
        if (connection == null) connection = DriverManager.getConnection("jdbc:sqlite:$path/database.db")
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

}