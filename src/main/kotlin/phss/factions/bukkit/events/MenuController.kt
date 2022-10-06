package phss.factions.bukkit.events

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.server.PluginDisableEvent
import phss.factions.bukkit.view.menu.getMenuFromInventory
import phss.factions.bukkit.view.menu.getMenuFromPlayer
import phss.factions.bukkit.view.menu.slot.MenuPlayerSlotInteract
import phss.factions.bukkit.view.menu.slotOrBaseSlot
import phss.factions.bukkit.view.menu.takeIfHasPlayer
import java.lang.Exception

class MenuController : Listener {

    @EventHandler
    fun pluginDisableEvent(event: PluginDisableEvent) {
        try {
            Bukkit.getOnlinePlayers().forEach {
                val menu = getMenuFromPlayer(it)
                menu?.close(it, true)
            }
        } catch (ignored: Exception) {}
    }

    @EventHandler(ignoreCancelled = true)
    fun clickInventoryEvent(event: InventoryClickEvent) {
        if (event.view.type == InventoryType.CHEST) {
            val player = event.whoClicked as Player
            val inv = event.inventory

            val menu = getMenuFromInventory(event.inventory)?.takeIfHasPlayer(player)

            if (menu != null) {
                if (event.rawSlot == event.slot) {
                    val clickedSlot = event.slot + 1
                    val slot = menu.slotOrBaseSlot(clickedSlot)

                    val interact = MenuPlayerSlotInteract(
                        menu, clickedSlot, slot,
                        player, inv, slot.cancel,
                        event.click, event.action,
                        event.currentItem, event.cursor,
                        event.hotbarButton
                    )

                    slot.eventHandler.interact(interact)

                    if (interact.canceled) event.isCancelled = true
                } else {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun clickInteractEvent(event: InventoryDragEvent) {
        if (event.view.type == InventoryType.CHEST) {
            val player = event.whoClicked as Player
            val menu = getMenuFromInventory(event.inventory)?.takeIfHasPlayer(player)
            if (menu != null) {
                val pass = event.inventorySlots.firstOrNull { it in event.rawSlots }
                if (pass != null) event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.view.type == InventoryType.CHEST) {
            val player = event.player as Player
            getMenuFromPlayer(player)?.close(player, false)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPickupItemEvent(event: PlayerPickupItemEvent) {
        if (getMenuFromPlayer(event.player) != null) event.isCancelled = true
    }

}