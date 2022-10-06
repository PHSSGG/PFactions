package phss.factions.config.providers.menus

import org.bukkit.configuration.file.FileConfiguration
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.utils.extensions.replaceColor

class FactionMembersMenuConfig(
    private val settings: FileConfiguration
) {

    val factionMembersMenu = FactionMembersMenu(
        settings.getString("MembersMenu.name")!!.replaceColor(),
        settings.getInt("MembersMenu.rows"),
        settings.getInt("MembersMenu.startLine"), settings.getInt("MembersMenu.endLine"), settings.getInt("MembersMenu.startSlot"), settings.getInt("MembersMenu.endSlot"),
        ItemSchema.buildItemByConfigSection("MembersMenu.memberItem", settings, "memberItem"),
        ItemSchema.buildItemByConfigSection("MembersMenu.nextItem", settings, "nextItem"), ItemSchema.buildItemByConfigSection("MembersMenu.previousItem", settings, "previousItem"),
        retrieveOrnamentItems("MembersMenu.ornamentItems")
    )

    private fun retrieveOrnamentItems(path: String): List<ItemSchema> {
        val list = ArrayList<ItemSchema>()
        for (selected in settings.getConfigurationSection(path)!!.getKeys(false))
            list.add(ItemSchema.buildItemByConfigSection("$path.$selected", settings, selected))

        return list
    }

    class FactionMembersMenu(
        val name: String,
        val rows: Int,
        val startLine: Int, val endLine: Int,
        val startSlot: Int, val endSlot: Int,
        val memberItem: ItemSchema,
        val nextItemSchema: ItemSchema, val previousItemSchema: ItemSchema,
        val ornamentItems: List<ItemSchema>
    )

}