package phss.factions.config.providers

import org.bukkit.configuration.file.FileConfiguration

class SpecialActionConfig(
    settings: FileConfiguration,
    key: String
) {

    private val path = "Special.$key"

    val configuration = SpecialConfiguration(
        key,
        settings.getStringList("$path.effects"),
        SpecialConfiguration.SpecialRequirement(settings.getStringList("$path.requirements.eat"), settings.getStringList("$path.requirements.armor")),
        SpecialConfiguration.SpecialDisplay(
            settings.getString("$path.display.name")!!
        )
    )

    class SpecialConfiguration(
        val key: String,
        val effects: List<String>,
        val requirements: SpecialRequirement,
        val display: SpecialDisplay
    ) {

        class SpecialRequirement(
            val eat: List<String>,
            val armor: List<String>
        )

        class SpecialDisplay(
            val name: String
        )

    }

}