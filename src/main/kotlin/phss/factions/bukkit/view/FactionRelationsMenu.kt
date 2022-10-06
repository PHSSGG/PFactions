package phss.factions.bukkit.view

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.config.providers.menus.FactionRelationsMenuConfig
import phss.factions.data.domain.Faction
import phss.factions.faction.FactionRelation
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.faction.types.RelationType
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.utils.extensions.replace
import phss.factions.collections.ObservableList
import phss.factions.bukkit.view.menu.dsl.menu
import phss.factions.bukkit.view.menu.dsl.slot
import phss.factions.bukkit.view.menu.dsl.pagination.pagination
import phss.factions.bukkit.view.menu.dsl.pagination.slot
import phss.factions.bukkit.view.menu.slot.MenuPlayerInventorySlot
import java.util.*

fun Player.openFactionRelationsMenu(plugin: PFactions, sortedType: Int = 0, desiredFaction: Faction? = null): Unit = with(plugin) {
    val settings = FactionRelationsMenuConfig(plugin.factionRelationsMenu.get).factionRelationsMenu
    val userAccount = userManager.getUserAccountByUUID(uniqueId) ?: return
    val faction = desiredFaction ?: factionManager.getFactionById(userAccount.factionPlayer!!.factionId) ?: return
    val relations = faction.relations.getSortedRelations(sortedType)

    menu(settings.name, settings.rows, plugin) {
        for (ornament in settings.ornamentItems) {
            if (ornament.key.lowercase().contains("filter_")) slot(ornament.slot, ornament.buildItem()).onClick {
                var filterType = ornament.key.lowercase().replace("filter_", "").toIntOrNull()
                if (filterType == null || filterType !in 0..4) filterType = 0

                close(player, true)
                player.openFactionRelationsMenu(plugin, filterType, desiredFaction)
                return@onClick
            }
            else slot(ornament.slot, ornament.buildItem())
        }

        val nextItemSlot = slot(settings.nextItemSchema.slot, settings.nextItemSchema.buildItem())
        val previousItemSlot = slot(settings.previousItemSchema.slot, settings.previousItemSchema.buildItem())

        val pagination = pagination(
            ObservableList(relations.toMutableList()),
            nextItemSlot, previousItemSlot,
            startLine = settings.startLine,
            endLine = settings.endLine,
            startSlot = settings.startSlot,
            endSlot = settings.endSlot
        ) {
            slot {
                onRender {
                    if (it != null) {
                        val relationFaction = plugin.factionManager.getFactionById(it.factionId)
                        if (relationFaction != null) {
                            val itemSchema = ItemSchema.createNewInstance(settings.relationItem)
                            val author = Bukkit.getOfflinePlayer(it.author)
                            itemSchema.name = itemSchema.name.replaceFactionInfo(relationFaction, plugin).replace("{relation}", it.type.toString()).replace("{author}", author.name ?: "null")
                            itemSchema.lore = itemSchema.lore.replaceFactionInfo(relationFaction, plugin).replace("{relation}", it.type.toString()).replace("{author}", author.name ?: "null")

                            showingItem = itemSchema.buildItem()
                        }
                    }
                }

                onClick {}

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
    }.openToPlayer(this@openFactionRelationsMenu)
}

/**
 * Function to get all the relations sorted by the type
 * @receiver ArrayList<FactionRelation> is the relations list
 *
 * @param sortedType is the sort type:
 *          0 = ALLY, TRADING, OUT_OF_WAR, HOSTILE and ENEMY
 *          1 = TRADING, ALLY, OUT_OF_WAR, HOSTILE and ENEMY
 *          2 = OUT_OF_WAR, ALLY, TRADING, HOSTILE and ENEMY
 *          3 = HOSTILE, ENEMY, ALLY, TRADING AND OUT_OF_WAR
 *          4 = ENEMY, HOSTILE, ALLY, TRADING AND OUT_OF_WAR
 *
 * @return a linked list of the sorted relations
 */
private fun ArrayList<FactionRelation>.getSortedRelations(sortedType: Int): LinkedList<FactionRelation> {
    val relations = LinkedList<FactionRelation>()

    when (sortedType) {
        0 -> {
            relations.addAll(this.filter { it.type == RelationType.ALLY })
            relations.addAll(this.filter { it.type == RelationType.TRADING })
            relations.addAll(this.filter { it.type == RelationType.OUT_OF_WAR })
            relations.addAll(this.filter { it.type == RelationType.HOSTILE })
            relations.addAll(this.filter { it.type == RelationType.ENEMY })
        }
        1 -> {
            relations.addAll(this.filter { it.type == RelationType.TRADING })
            relations.addAll(this.filter { it.type == RelationType.ALLY })
            relations.addAll(this.filter { it.type == RelationType.OUT_OF_WAR })
            relations.addAll(this.filter { it.type == RelationType.HOSTILE })
            relations.addAll(this.filter { it.type == RelationType.ENEMY })
        }
        2 -> {
            relations.addAll(this.filter { it.type == RelationType.OUT_OF_WAR })
            relations.addAll(this.filter { it.type == RelationType.ALLY })
            relations.addAll(this.filter { it.type == RelationType.TRADING })
            relations.addAll(this.filter { it.type == RelationType.HOSTILE })
            relations.addAll(this.filter { it.type == RelationType.ENEMY })
        }
        3 -> {
            relations.addAll(this.filter { it.type == RelationType.HOSTILE })
            relations.addAll(this.filter { it.type == RelationType.ENEMY })
            relations.addAll(this.filter { it.type == RelationType.ALLY })
            relations.addAll(this.filter { it.type == RelationType.TRADING })
            relations.addAll(this.filter { it.type == RelationType.OUT_OF_WAR })
        }
        4 -> {
            relations.addAll(this.filter { it.type == RelationType.ENEMY })
            relations.addAll(this.filter { it.type == RelationType.HOSTILE })
            relations.addAll(this.filter { it.type == RelationType.ALLY })
            relations.addAll(this.filter { it.type == RelationType.TRADING })
            relations.addAll(this.filter { it.type == RelationType.OUT_OF_WAR })
        }
    }

    return relations
}