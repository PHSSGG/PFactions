package phss.factions.faction.special.actions

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import phss.factions.PFactions
import phss.factions.config.providers.SpecialActionConfig
import phss.factions.faction.special.SpecialAction
import java.util.*

class ZombieSpecialAction(
    private val plugin: PFactions,
    override val key: String = "zombie",
    override val configuration: SpecialActionConfig.SpecialConfiguration? = SpecialActionConfig(plugin.settings.get, key).configuration
) : SpecialAction {

    override fun canExecute(playerUUID: UUID): Boolean {
        val factionPlayer = plugin.userManager.getUserAccountByUUID(playerUUID)?.factionPlayer ?: return false
        val faction = plugin.factionManager.getFactionById(factionPlayer.factionId) ?: return false
        return faction.type == key && faction.leader == playerUUID
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (canExecute(event.player.uniqueId)) invoke(event.player.uniqueId)
    }

}