package phss.factions.config.providers

import org.bukkit.configuration.file.FileConfiguration
import phss.factions.utils.extensions.replaceColor

class BossConfig(
    private val settings: FileConfiguration,
    key: String
) {

    private val path = "Bosses.$key"

    val configuration = Configuration(
        Configuration.Display(
            settings.getString("$path.display.name")!!.replaceColor(), settings.getStringList("$path.display.lore").replaceColor(),
            settings.getString("$path.display.id")!!, settings.getInt("$path.display.data"), settings.getString("$path.display.skullOwner")!!,
            settings.getBoolean("$path.display.glow")
        ),
        settings.getString("$path.type")!!,
        settings.getDouble("$path.health"),
        settings.getDouble("$path.damage"),
        settings.getBoolean("$path.hasCritical"),
        settings.getInt("$path.criticalChance"),
        settings.getDouble("$path.criticalDamage"),
        settings.getBoolean("$path.canThrow"),
        settings.getDouble("$path.throwItemDamage"),
        settings.getInt("$path.throwDelay")
    )

    fun exists(): Boolean {
        return settings.getConfigurationSection(path) != null
    }

    class Configuration(
        val display: Display,
        val type: String,
        val health: Double,
        val damage: Double,
        val hasCritical: Boolean,
        val criticalChance: Int,
        val criticalDamage: Double,
        val canThrow: Boolean,
        val throwDamage: Double,
        val throwDelay: Int
    ) {

        class Display(
            val name: String,
            val lore: List<String>,
            val id: String,
            val data: Int,
            val skullOwner: String,
            val glow: Boolean
        )

    }

}