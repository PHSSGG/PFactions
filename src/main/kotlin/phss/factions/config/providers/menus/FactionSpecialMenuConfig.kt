package phss.factions.config.providers.menus

import org.bukkit.configuration.file.FileConfiguration
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.utils.extensions.replaceColor

class FactionSpecialMenuConfig(
    private val settings: FileConfiguration
) {

    val selectTypeMenu = SelectTypeMenu(
        settings.getString("SelectType.name")!!.replaceColor(),
        settings.getInt("SelectType.rows"),
        retrieveItemsList("SelectType.items"),
        retrieveItemsList("SelectType.ornamentItems")
    )

    val bossMenu = BossMenu(
        settings.getString("BossMenu.name")!!.replaceColor(),
        settings.getInt("BossMenu.rows"),
        settings.getInt("BossMenu.bossItem.slot"),
        retrieveItemsList("BossMenu.ornamentItems")
    )

    val controlMenu = ControlMenu(
        settings.getString("ControlMenu.name")!!.replaceColor(),
        settings.getInt("ControlMenu.rows"),
        retrieveItemsList("ControlMenu.items")
    )

    private fun retrieveItemsList(path: String): List<ItemSchema> {
        val list = ArrayList<ItemSchema>()

        if (settings.getConfigurationSection(path) == null) return list
        for (selected in settings.getConfigurationSection(path)!!.getKeys(false))
            list.add(ItemSchema.buildItemByConfigSection("$path.$selected", settings, selected))

        return list
    }

    class SelectTypeMenu(
        val name: String,
        val rows: Int,
        val items: List<ItemSchema>,
        val ornamentItems: List<ItemSchema>
    )

    class BossMenu(
        val name: String,
        val rows: Int,
        val bossItemSlot: Int,
        val ornamentItems: List<ItemSchema>
    )

    class ControlMenu(
        val name: String,
        val rows: Int,
        val items: List<ItemSchema>
    )

}