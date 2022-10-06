package phss.factions.config.providers

import org.bukkit.configuration.file.FileConfiguration

class DatabaseConfig(
    config: FileConfiguration
) {

    val database = with(config) {
        Database(
            getBoolean("Storage.mysql", false),
            getString("Storage.hostname", "localhost")!!, getInt("Storage.port", 3306), getString("Storage.database", "factions")!!,
            getString("Storage.username", "root")!!, getString("Storage.password", "")!!
        )
    }

    class Database(
        val mysql: Boolean,
        val hostname: String,
        val port: Int,
        val database: String,
        val username: String,
        val password: String
    )

}