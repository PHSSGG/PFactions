package phss.factions.bukkit.view

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.config.providers.menus.FactionMembersMenuConfig
import phss.factions.conversation.FactionConversation
import phss.factions.conversation.providers.*
import phss.factions.data.domain.Faction
import phss.factions.data.domain.User
import phss.factions.faction.extensions.replaceMemberInfo
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.collections.ObservableList
import phss.factions.bukkit.view.menu.dsl.menu
import phss.factions.bukkit.view.menu.dsl.slot
import phss.factions.bukkit.view.menu.dsl.pagination.pagination
import phss.factions.bukkit.view.menu.dsl.pagination.slot
import phss.factions.bukkit.view.menu.slot.MenuPlayerInventorySlot
import java.util.*
import kotlin.collections.ArrayList

private val ornamentActionsKey = listOf(
    "invite", "kick", "ban", "unban"
)
fun Player.openFactionMembersMenu(plugin: PFactions, desiredFaction: Faction? = null) = with(plugin) {
    val settings = FactionMembersMenuConfig(plugin.factionMembersMenu.get).factionMembersMenu
    val userAccount = userManager.getUserAccountByUUID(uniqueId) ?: return
    val faction = desiredFaction ?: factionManager.getFactionById(userAccount.factionPlayer!!.factionId) ?: return

    menu(settings.name, settings.rows, plugin) {
        for (ornament in settings.ornamentItems) {
            if (!ornamentActionsKey.contains(ornament.key.lowercase())) slot(ornament.slot, ornament.buildItem())
            else if (userAccount.factionPlayer != null && userAccount.factionPlayer!!.factionId == faction.id && userAccount.factionPlayer!!.role.permissionLevel >= 1) {
                when (ornament.key.lowercase()) {
                    "invite" -> slot(ornament.slot, ornament.buildItem()).onClick {
                        close(this@openFactionMembersMenu, true)
                        FactionConversation(plugin, this@openFactionMembersMenu, FactionInviteConversation()).begin()
                    }
                    "kick" -> slot(ornament.slot, ornament.buildItem()).onClick {
                        close(this@openFactionMembersMenu, true)
                        FactionConversation(plugin, this@openFactionMembersMenu, FactionKickConversation()).begin()
                    }
                    "ban" -> slot(ornament.slot, ornament.buildItem()).onClick {
                        close(this@openFactionMembersMenu, true)
                        FactionConversation(plugin, this@openFactionMembersMenu, FactionBanConversation()).begin()
                    }
                    "unban" -> slot(ornament.slot, ornament.buildItem()).onClick {
                        close(this@openFactionMembersMenu, true)
                        FactionConversation(plugin, this@openFactionMembersMenu, FactionUnbanConversation()).begin()
                    }
                }
            }
        }

        val nextItemSlot = slot(settings.nextItemSchema.slot, settings.nextItemSchema.buildItem())
        val previousItemSlot = slot(settings.previousItemSchema.slot, settings.previousItemSchema.buildItem())

        val pagination = pagination(
            ObservableList(faction.members.retrieveFactionMembers(plugin).sortedByDescending { it.second.factionPlayer?.role?.permissionLevel ?: 0 }.toMutableList()),
            nextItemSlot, previousItemSlot,
            startLine = settings.startLine,
            endLine = settings.endLine,
            startSlot = settings.startSlot,
            endSlot = settings.endSlot
        ) {
            slot {
                onRender {
                    if (it != null) {
                        val itemSchema = ItemSchema.createNewInstance(settings.memberItem)
                        itemSchema.name = itemSchema.name.replaceMemberInfo(it.second, plugin)
                        itemSchema.lore = itemSchema.lore.replaceMemberInfo(it.second, plugin)

                        showingItem = itemSchema.buildItem()
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
    }.openToPlayer(this@openFactionMembersMenu)
}

/**
 * Function to get a list of uuids of the players in the faction
 * @receiver List<UUID> is the list of desired players
 * @param plugin is the FactionsPlugin instance
 *
 * @return a list of pairs containing the OfflinePlayer and the User account of the faction member
 */
private fun List<UUID>.retrieveFactionMembers(plugin: PFactions): List<Pair<OfflinePlayer, User>> {
    val list = ArrayList<Pair<OfflinePlayer, User>>()
    for (uuid in this) {
        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
        val user = plugin.userManager.getUserAccountByUUID(uuid) ?: continue

        list.add(offlinePlayer to user)
    }

    return list
}