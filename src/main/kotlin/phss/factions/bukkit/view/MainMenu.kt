package phss.factions.bukkit.view

import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.view.menu.dsl.menu
import phss.factions.bukkit.view.menu.dsl.slot
import phss.factions.config.providers.menus.MainMenuConfig
import phss.factions.conversation.FactionConversation
import phss.factions.conversation.providers.FactionCreateConversation
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.faction.extensions.replaceMemberInfo
import phss.factions.faction.permission.PlayerRole
import phss.factions.utils.extensions.sendMessage

fun Player.openWithoutFactionMenu(plugin: PFactions) = with(plugin) {
    val settings = MainMenuConfig(plugin.mainMenu.get).withoutFactionMainMenu

    menu(settings.name, settings.rows, plugin) {
        for (ornament in settings.ornamentItems) slot(ornament.slot, ornament.buildItem())
        for (item in settings.items) {
            slot(item.slot, item.buildItem()).onClick {
                when (item.key.lowercase()) {
                    "create" -> {
                        close(player, true)
                        FactionConversation(plugin, player, FactionCreateConversation()).begin()
                    }
                    "top" -> openFactionTopMenu(plugin)
                    "help" -> {
                        close(player, true)
                        sendMessage(plugin.messages.getMessageList("help.default"))
                    }
                }
            }
        }
    }.openToPlayer(this@openWithoutFactionMenu)
}

fun Player.openWithFactionMenu(plugin: PFactions) = with(plugin) {
    val settings = MainMenuConfig(plugin.mainMenu.get).withFactionMainMenu
    val userAccount = userManager.getUserAccountByUUID(uniqueId) ?: return
    val faction = factionManager.getFactionById(userAccount.factionPlayer!!.factionId) ?: return

    menu(settings.name.replaceFactionInfo(faction, plugin), settings.rows, plugin) {
        for (ornament in settings.ornamentItems) slot(ornament.slot, ornament.buildItem())
        for (item in settings.items) {
            item.name = item.name.replaceMemberInfo(userAccount, plugin).replaceFactionInfo(faction, plugin)
            item.lore = item.lore.replaceMemberInfo(userAccount, plugin).replaceFactionInfo(faction, plugin)

            if (item.key.lowercase() == "leave" && userAccount.factionPlayer!!.role == PlayerRole.EXILE) continue
            if (item.key.lowercase() == "disband" && userAccount.factionPlayer!!.role != PlayerRole.EXILE) continue
            if (item.key.lowercase() == "type") {
                if (faction.type != null) {
                    if (faction.boss == null) item.lore = plugin.mainMenu.get.getStringList("WithFaction.items.type.loreSelected").replaceMemberInfo(userAccount, plugin).replaceFactionInfo(faction, plugin)
                    else item.lore = plugin.mainMenu.get.getStringList("WithFaction.items.type.loreHasBoss").replaceMemberInfo(userAccount, plugin).replaceFactionInfo(faction, plugin)
                }
            }

            slot(item.slot, item.buildItem()).onClick {
                when (item.key.lowercase()) {
                    "top" -> openFactionTopMenu(plugin)
                    "members" -> openFactionMembersMenu(plugin)
                    "relations" -> openFactionRelationsMenu(plugin)
                    "type" -> {
                        if (faction.boss != null && faction.type != null) {
                            openFactionBossMenu(plugin, faction)
                            return@onClick
                        }
                        if (faction.type == null) {
                            openFactionSelectTypeMenu(plugin, faction)
                            return@onClick
                        }
                    }
                    "help" -> {
                        close(player, true)
                        sendMessage(plugin.messages.getMessageList("help.default"))
                    }
                    "leave", "disband" -> {
                        // the player can be removed from the faction while this menu is opened
                        if (userAccount.factionPlayer == null || factionManager.getFactionById(userAccount.factionPlayer!!.factionId) == null) {
                            close(player, true)
                            return@onClick
                        }

                        if (userAccount.factionPlayer!!.role == PlayerRole.EXILE) player.chat("/f disband")
                        else player.chat("/f leave")

                        close(player, true)
                    }
                }
            }
        }
    }.openToPlayer(this@openWithFactionMenu)
}