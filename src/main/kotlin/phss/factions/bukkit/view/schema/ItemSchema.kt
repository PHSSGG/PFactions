package phss.factions.bukkit.view.schema

import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import phss.factions.utils.extensions.replaceColor
import phss.factions.utils.ItemBuilder
import phss.factions.utils.SkullUtils

class ItemSchema(
    var name: String,
    var lore: List<String> = arrayListOf(),
    var id: String = "",
    var data: Int = 0,
    var skullOwner: String = "null",
    var glow: Boolean = false,
    var slot: Int = 1,
    var key: String = name
) {

    fun buildItem(): ItemStack {
        var item = if (skullOwner.startsWith("http"))
            ItemBuilder(SkullUtils().getSkull(skullOwner))
                .setName(name.replaceColor())
                .setLore(lore.replaceColor())
                .build()!!
        else ItemBuilder(Material.getMaterial(id.uppercase())!!)
            .setDurability(data).setSkullOwner(skullOwner)
            .setName(name.replaceColor())
            .setLore(lore.replaceColor())
            .build()!!
        if (glow) item = setGlowing(item)

        return item
    }

    companion object {
        fun buildItemByConfigSection(path: String, settings: FileConfiguration, key: String = "") = ItemSchema(
            settings.getString("$path.name")!!,
            settings.getStringList("$path.lore"),
            settings.getString("$path.id")!!,
            settings.getInt("$path.data"),
            settings.getString("$path.skullOwner")!!,
            settings.getBoolean("$path.glow"),
            settings.getInt("$path.slot", 1),
            key
        )

        fun setGlowing(item: ItemStack): ItemStack {
            item.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1)
            val meta = item.itemMeta
            meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            item.itemMeta = meta

            return item
        }

        fun createNewInstance(itemSchema: ItemSchema) = ItemSchema(
            itemSchema.name, itemSchema.lore,
            itemSchema.id, itemSchema.data, itemSchema.skullOwner,
            itemSchema.glow,
            itemSchema.slot,
            itemSchema.key
        )
    }

}