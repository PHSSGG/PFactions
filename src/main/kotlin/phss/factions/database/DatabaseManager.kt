package phss.factions.database

import phss.factions.PFactions
import phss.factions.config.providers.DatabaseConfig
import phss.factions.database.controller.DataController
import phss.factions.database.impl.MySQLDatabaseImpl
import phss.factions.database.impl.SQLiteDatabaseImpl
import phss.factions.database.process.DataProcess

class DatabaseManager(
    private val plugin: PFactions
) {

    companion object {
        const val FACTIONS_TABLE = "factions"
        const val USERS_TABLE = "users"
    }

    lateinit var database: Database
    lateinit var controller: DataController
    lateinit var process: DataProcess

    fun start() {
        val databaseConfig = DatabaseConfig(plugin.storage.get).database

        database = if (databaseConfig.mysql) MySQLDatabaseImpl(databaseConfig)
        else SQLiteDatabaseImpl("${plugin.dataFolder.path}/storage")

        if (database.open() != null) {
            database.statement?.execute("CREATE TABLE IF NOT EXISTS $FACTIONS_TABLE (id INT, name TEXT, leader VARCHAR(36), display TEXT, motd TEXT, home TEXT, power DOUBLE, max_power DOUBLE, member_cap INT, type TEXT, boss TEXT, members LONGTEXT, bans LONGTEXT, claims LONGTEXT, relations LONGTEXT, wars LONGTEXT, created_at LONG);")
            database.statement?.execute("CREATE TABLE IF NOT EXISTS $USERS_TABLE (uuid VARCHAR(36), name VARCHAR(32), faction_player TEXT, max_power DOUBLE, power DOUBLE, kills INT, deaths INT, temp_join_ban LONG, last_seen LONG);")
            database.close()
        }

        controller = DataController(database)
        process = DataProcess(controller)
    }

}