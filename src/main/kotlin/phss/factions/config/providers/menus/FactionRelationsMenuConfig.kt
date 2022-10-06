package phss.factions.config.providers.menus

import org.bukkit.configuration.file.FileConfiguration
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.utils.extensions.replaceColor

class FactionRelationsMenuConfig(
    private val settings: FileConfiguration
) {

    val factionRelationsMenu = FactionRelationsMenu(
        settings.getString("RelationsMenu.name")!!.replaceColor(),
        settings.getInt("RelationsMenu.rows"),
        settings.getInt("RelationsMenu.startLine"), settings.getInt("RelationsMenu.endLine"), settings.getInt("RelationsMenu.startSlot"), settings.getInt("RelationsMenu.endSlot"),
        ItemSchema.buildItemByConfigSection("RelationsMenu.relationItem", settings, "relationItem"),
        ItemSchema.buildItemByConfigSection("RelationsMenu.nextItem", settings, "nextItem"), ItemSchema.buildItemByConfigSection("RelationsMenu.previousItem", settings, "previousItem"),
        retrieveOrnamentItems("RelationsMenu.ornamentItems")
    )

    private fun retrieveOrnamentItems(path: String): List<ItemSchema> {
        val list = ArrayList<ItemSchema>()
        for (selected in settings.getConfigurationSection(path)!!.getKeys(false))
            list.add(ItemSchema.buildItemByConfigSection("$path.$selected", settings, selected))

        return list
    }

    class FactionRelationsMenu(
        val name: String,
        val rows: Int,
        val startLine: Int, val endLine: Int,
        val startSlot: Int, val endSlot: Int,
        val relationItem: ItemSchema,
        val nextItemSchema: ItemSchema, val previousItemSchema: ItemSchema,
        val ornamentItems: List<ItemSchema>
    )

}