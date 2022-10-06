package phss.factions.bukkit.view

import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.config.providers.menus.FactionInfoMenuConfig
import phss.factions.data.domain.Faction
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.bukkit.view.menu.dsl.menu
import phss.factions.bukkit.view.menu.dsl.slot

fun Player.openFactionInfoMenu(plugin: PFactions, faction: Faction) = with(plugin) {
    val settings = FactionInfoMenuConfig(plugin.factionInfoMenu.get).infoMenu

    menu(settings.name, settings.rows, plugin) {
        for (item in settings.items) {
            item.name = item.name.replaceFactionInfo(faction, plugin)
            item.lore = item.lore.replaceFactionInfo(faction, plugin)

            when (item.key.lowercase()) {
                "relations" -> slot(item.slot, item.buildItem()).onClick { openFactionRelationsMenu(plugin, desiredFaction = faction) }
                "members" -> slot(item.slot, item.buildItem()).onClick { openFactionMembersMenu(plugin, desiredFaction = faction) }
                else -> slot(item.slot, item.buildItem())
            }
        }
    }.openToPlayer(this@openFactionInfoMenu)
}