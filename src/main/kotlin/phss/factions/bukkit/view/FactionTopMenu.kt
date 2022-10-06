package phss.factions.bukkit.view

import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.config.providers.menus.FactionTopMenuConfig
import phss.factions.data.domain.Faction
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.collections.ObservableList
import phss.factions.bukkit.view.menu.dsl.menu
import phss.factions.bukkit.view.menu.dsl.slot
import phss.factions.bukkit.view.menu.dsl.pagination.pagination
import phss.factions.bukkit.view.menu.dsl.pagination.slot
import phss.factions.bukkit.view.menu.slot.MenuPlayerInventorySlot

fun Player.openFactionTopMenu(plugin: PFactions, sortedType: Int = 0): Unit = with(plugin) {
    val settings = FactionTopMenuConfig(plugin.factionTopMenu.get).factionTopMenu

    menu(settings.name, settings.rows, plugin) {
        for (ornament in settings.ornamentItems) {
            if (ornament.key.lowercase().contains("filter_")) slot(ornament.slot, ornament.buildItem()).onClick {
                var filterType = ornament.key.lowercase().replace("filter_", "").toIntOrNull()
                if (filterType == null || filterType !in 0..2) filterType = 0

                close(player, true)
                player.openFactionTopMenu(plugin, filterType)
                return@onClick
            }
            else slot(ornament.slot, ornament.buildItem())
        }

        val nextItemSlot = slot(settings.nextItemSchema.slot, settings.nextItemSchema.buildItem())
        val previousItemSlot = slot(settings.previousItemSchema.slot, settings.previousItemSchema.buildItem())

        val pagination = pagination(
            ObservableList(factionManager.getFactions().getSortedFactions(sortedType).toMutableList()),
            nextItemSlot, previousItemSlot,
            startLine = settings.startLine,
            endLine = settings.endLine,
            startSlot = settings.startSlot,
            endSlot = settings.endSlot
        ) {
            slot {
                onRender {
                    if (it != null) {
                        val itemSchema = ItemSchema.createNewInstance(settings.factionItem)
                        itemSchema.name = itemSchema.name.replaceFactionInfo(it, plugin)
                        itemSchema.lore = itemSchema.lore.replaceFactionInfo(it, plugin)

                        showingItem = itemSchema.buildItem()
                    }
                }

                onClick {
                    if (it != null) openFactionInfoMenu(plugin, it)
                }

                onPageChange {
                    updateSlot(previousPageSlot, player)
                    updateSlot(nextPageSlot, player)
                }
            }
        }

        // set previous and next items
        fun MenuPlayerInventorySlot.showItemWhenHasPage(type: String, hasPageLogic: () -> Boolean) {
            showingItem = if (hasPageLogic()) when (type) {
                "next" -> nextItemSlot.item
                "previous" -> previousItemSlot.item
                else -> null
            }
            else null
        }

        nextItemSlot.onRender { showItemWhenHasPage("next") { pagination.hasNextPage(player) } }
        nextItemSlot.onUpdate { showItemWhenHasPage("next") { pagination.hasNextPage(player) } }
        previousItemSlot.onRender { showItemWhenHasPage("previous") { pagination.hasPreviousPage(player) } }
        previousItemSlot.onUpdate { showItemWhenHasPage("previous") { pagination.hasPreviousPage(player) } }
    }.openToPlayer(this@openFactionTopMenu)
}

/**
 * Function to get all the factions sorted by the type
 * @receiver List<Faction> is the factions list
 *
 * @param sortedType is the sort type:
 *          0 = power
 *          1 = members
 *          2 = claims
 *          3 = created date
 *
 * @return a list of the sorted by descending factions
 */
fun List<Faction>.getSortedFactions(sortedType: Int): List<Faction> {
    return when (sortedType) {
        0 -> this.sortedByDescending { it.power }
        1 -> this.sortedByDescending { it.members.size }
        2 -> this.sortedByDescending { it.claims.size }
        else -> this.sortedByDescending { it.createdAt }
    }
}