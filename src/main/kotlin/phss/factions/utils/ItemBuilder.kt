package phss.factions.utils

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemBuilder {

    private var itemStack: ItemStack? = null

    constructor(itemStack: ItemStack) {
        this.itemStack = itemStack
    }

    @JvmOverloads
    constructor(material: Material, amount: Int = 1) {
        itemStack = ItemStack(material, amount)
    }

    constructor(material: Material, amount: Int, durability: Byte) {
        itemStack = ItemStack(material, amount, durability.toShort())
    }


    constructor(material: Material, amount: Int, durability: Int) {
        itemStack = ItemStack(material, amount, durability.toShort())
    }

    fun setDurability(durability: Short): ItemBuilder {
        itemStack!!.durability = durability
        return this
    }

    fun setAmount(amount: Int): ItemBuilder {
        itemStack!!.amount = amount
        val itemMeta = itemStack!!.itemMeta
        itemMeta?.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
        itemStack!!.itemMeta = itemMeta
        return this
    }

    fun setDurability(durability: Int): ItemBuilder {
        itemStack!!.durability = java.lang.Short.valueOf("" + durability)
        return this
    }

    fun setName(name: String): ItemBuilder {
        val itemMeta = itemStack!!.itemMeta
        itemMeta?.setDisplayName(name)
        itemStack!!.itemMeta = itemMeta
        return this
    }

    fun addUnsafeEnchantment(enchantment: Enchantment, level: Int): ItemBuilder {
        itemStack!!.addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun removeEnchantment(enchantment: Enchantment): ItemBuilder {
        itemStack!!.removeEnchantment(enchantment)
        return this
    }

    fun setSkullOwner(owner: String): ItemBuilder {
        try {
            val itemMeta = itemStack!!.itemMeta as SkullMeta
            itemMeta.owner = owner
            itemStack!!.itemMeta = itemMeta
        } catch (expected: ClassCastException) {
        }

        return this
    }

    fun addEnchant(enchantment: Enchantment, level: Int): ItemBuilder {
        val itemMeta = itemStack!!.itemMeta
        itemMeta?.addEnchant(enchantment, level, true)
        itemStack!!.itemMeta = itemMeta
        return this
    }

    fun addEnchantments(enchantments: Map<Enchantment, Int>): ItemBuilder {
        itemStack!!.addEnchantments(enchantments)
        return this
    }

    fun addItemFlag(flag: ItemFlag): ItemBuilder {
        val itemMeta = itemStack!!.itemMeta!!
        itemMeta.addItemFlags(flag)
        itemStack!!.itemMeta = itemMeta
        return this
    }

    fun setLore(lines: List<String>): ItemBuilder {
        val itemMeta = itemStack!!.itemMeta
        var lore = ArrayList<String>()
        if (itemMeta!!.hasLore()) lore = ArrayList(itemMeta.lore)
        for (line in lines) {
            lore.add(line)
        }
        itemMeta.lore = lore
        itemStack!!.itemMeta = itemMeta
        return this
    }

    fun build(): ItemStack? {
        return itemStack
    }

}