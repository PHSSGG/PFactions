package phss.factions.config.providers.menus

import org.bukkit.configuration.file.FileConfiguration
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.utils.extensions.replaceColor

class FactionInfoMenuConfig(
    private val settings: FileConfiguration
) {

    val infoMenu = InfoMenu(
        settings.getString("InfoMenu.name")!!.replaceColor(),
        settings.getInt("InfoMenu.rows"),
        retrieveItemsList("InfoMenu.items"),
    )

    private fun retrieveItemsList(path: String): List<ItemSchema> {
        val list = ArrayList<ItemSchema>()

        if (settings.getConfigurationSection(path) == null) return list
        for (selected in settings.getConfigurationSection(path)!!.getKeys(false))
            list.add(ItemSchema.buildItemByConfigSection("$path.$selected", settings, selected))

        return list
    }

    class InfoMenu(
        val name: String,
        val rows: Int,
        val items: List<ItemSchema>,
    )

}