package phss.factions.database

import java.sql.Connection
import java.sql.Statement

interface Database {

    val type: String

    val connection: Connection?
    val statement: Statement?

    fun open(): Connection?
    fun close(): Boolean
    val isConnected: Boolean

}