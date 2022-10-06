package phss.factions.config.providers.menus

import org.bukkit.configuration.file.FileConfiguration
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.utils.extensions.replaceColor

class FactionTopMenuConfig(
    private val settings: FileConfiguration
) {

    val factionTopMenu = FactionTopMenu(
        settings.getString("TopMenu.name")!!.replaceColor(),
        settings.getInt("TopMenu.rows"),
        settings.getInt("TopMenu.startLine"), settings.getInt("TopMenu.endLine"), settings.getInt("TopMenu.startSlot"), settings.getInt("TopMenu.endSlot"),
        ItemSchema.buildItemByConfigSection("TopMenu.factionItem", settings, "factionItem"),
        ItemSchema.buildItemByConfigSection("TopMenu.nextItem", settings, "nextItem"), ItemSchema.buildItemByConfigSection("TopMenu.previousItem", settings, "previousItem"),
        retrieveOrnamentItems("TopMenu.ornamentItems")
    )

    private fun retrieveOrnamentItems(path: String): List<ItemSchema> {
        val list = ArrayList<ItemSchema>()
        for (selected in settings.getConfigurationSection(path)!!.getKeys(false))
            list.add(ItemSchema.buildItemByConfigSection("$path.$selected", settings, selected))

        return list
    }

    class FactionTopMenu(
        val name: String,
        val rows: Int,
        val startLine: Int, val endLine: Int,
        val startSlot: Int, val endSlot: Int,
        val factionItem: ItemSchema,
        val nextItemSchema: ItemSchema, val previousItemSchema: ItemSchema,
        val ornamentItems: List<ItemSchema>
    )

}