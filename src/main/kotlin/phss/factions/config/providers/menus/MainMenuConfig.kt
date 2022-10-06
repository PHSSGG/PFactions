package phss.factions.config.providers.menus

import org.bukkit.configuration.file.FileConfiguration
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.utils.extensions.replaceColor

class MainMenuConfig(
    private val settings: FileConfiguration
) {

    val withoutFactionMainMenu = MainMenu(
        settings.getString("WithoutFaction.name")!!.replaceColor(),
        settings.getInt("WithoutFaction.rows"),
        retrieveItemsList("WithoutFaction.items"),
        retrieveItemsList("WithoutFaction.ornamentItems")
    )

    val withFactionMainMenu = MainMenu(
        settings.getString("WithFaction.name")!!.replaceColor(),
        settings.getInt("WithFaction.rows"),
        retrieveItemsList("WithFaction.items"),
        retrieveItemsList("WithFaction.ornamentItems")
    )

    private fun retrieveItemsList(path: String): List<ItemSchema> {
        val list = ArrayList<ItemSchema>()

        if (settings.getConfigurationSection(path) == null) return list
        for (selected in settings.getConfigurationSection(path)!!.getKeys(false))
            list.add(ItemSchema.buildItemByConfigSection("$path.$selected", settings, selected))

        return list
    }

    class MainMenu(
        val name: String,
        val rows: Int,
        val items: List<ItemSchema>,
        val ornamentItems: List<ItemSchema>
    )

}