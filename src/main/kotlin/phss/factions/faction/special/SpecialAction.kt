package phss.factions.faction.special

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import phss.factions.config.providers.SpecialActionConfig
import java.util.*


interface SpecialAction : Listener {

    val key: String
    val configuration: SpecialActionConfig.SpecialConfiguration?

    fun canExecute(playerUUID: UUID): Boolean
    fun invoke(playerUUID: UUID) {
        if (configuration == null) return
        val player = Bukkit.getPlayer(playerUUID) ?: return

        configuration!!.effects.forEach {
            player.addPotionEffect(PotionEffect(PotionEffectType.getByName(it)!!, Integer.MAX_VALUE, 1))
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onEat(event: PlayerItemConsumeEvent) {
        if (configuration == null || configuration!!.requirements.eat.isEmpty() || !canExecute(event.player.uniqueId)) return

        if (event.item.type.name !in configuration!!.requirements.eat) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        if (!canExecute(event.whoClicked.uniqueId)) return

        var (isShift, isNumberKey) = false to false

        if (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT) isShift = true
        if (event.click == ClickType.NUMBER_KEY) isNumberKey = true

        if (event.slotType != InventoryType.SlotType.ARMOR && event.slotType != InventoryType.SlotType.QUICKBAR && event.slotType != InventoryType.SlotType.CONTAINER) return
        if (event.clickedInventory != null && event.clickedInventory!!.type != InventoryType.PLAYER) return
        if (event.inventory.type != InventoryType.CRAFTING && event.inventory.type != InventoryType.PLAYER) return

        var armorType = ArmorType.matchType(if (isShift) event.currentItem else event.cursor)
        if(!isShift && armorType != null && event.rawSlot != armorType.slot) return

        var canCancel = false

        if (isShift) {
            if (armorType != null) {
                if (event.rawSlot == armorType.slot) return
                canCancel = runOnUseArmor(event.currentItem!!.type.name)
            }
        } else {
            var newArmor = event.cursor
            if (isNumberKey) {
                if(event.clickedInventory!!.type != InventoryType.PLAYER) return
                val hotbarItem = event.clickedInventory!!.getItem(event.hotbarButton)

                if (hotbarItem != null && hotbarItem.type != Material.AIR) {
                    armorType = ArmorType.matchType(hotbarItem)
                    newArmor = hotbarItem
                } else armorType = ArmorType.matchType(if (event.currentItem != null && event.currentItem!!.type != Material.AIR) event.currentItem else event.cursor)
            } else {
                if ((event.cursor == null || event.cursor!!.type == Material.AIR) && (event.currentItem == null || event.currentItem!!.type == Material.AIR)) armorType = ArmorType.matchType(event.currentItem)
            }

            if (armorType != null && event.rawSlot == armorType.slot) {
                canCancel = runOnUseArmor(newArmor!!.type.name)
            }
        }

        if (canCancel) event.isCancelled = true
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInteract(event: PlayerInteractEvent) {
        if(event.useItemInHand() == Event.Result.DENY) return

        if(event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            val armorType = ArmorType.matchType(event.item) ?: return

            with(event.player.inventory) {
                if ((armorType == ArmorType.HELMET && (helmet != null && helmet!!.type != Material.AIR))
                    || (armorType == ArmorType.CHESTPLATE && (chestplate != null && chestplate!!.type != Material.AIR))
                    || (armorType == ArmorType.LEGGINGS && (leggings != null && leggings!!.type != Material.AIR))
                    || (armorType == ArmorType.BOOTS && (boots != null && boots!!.type != Material.AIR))
                    ) {
                    event.isCancelled = runOnUseArmor(event.item!!.type.name)
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryDrag(event: InventoryDragEvent) {
        if (event.rawSlots.isEmpty()) return

        val armorType = ArmorType.matchType(event.oldCursor)
        if (armorType != null && armorType.slot == event.rawSlots.firstOrNull()) {
            event.isCancelled = runOnUseArmor(event.oldCursor.type.name)
        }
    }

    private fun runOnUseArmor(armorType: String): Boolean {
        if (configuration == null || configuration!!.requirements.armor.isEmpty()) return false
        return configuration!!.requirements.armor.find { armorType.contains(it) } == null
    }

}

enum class ArmorType(val slot: Int) {
    HELMET(5), CHESTPLATE(6), LEGGINGS(7), BOOTS(8);

    companion object {
        fun matchType(itemStack: ItemStack?): ArmorType? {
            if (itemStack == null || itemStack.type == Material.AIR) return null
            val type = itemStack.type.name

            return when {
                type.endsWith("HELMET") -> HELMET
                type.endsWith("CHESTPLATE") -> CHESTPLATE
                type.endsWith("LEGGINGS") -> LEGGINGS
                type.endsWith("BOOTS") -> BOOTS
                else -> null
            }
        }
    }
}