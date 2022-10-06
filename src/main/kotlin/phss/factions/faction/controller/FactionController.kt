package phss.factions.faction.controller

import phss.factions.PFactions
import phss.factions.data.domain.Faction
import phss.factions.data.domain.User
import phss.factions.faction.FactionPlayer
import phss.factions.faction.permission.PlayerRole
import java.util.concurrent.TimeUnit
import kotlin.math.min

class FactionController(
    private val plugin: PFactions
) {

    fun addPlayerToFaction(user: User, faction: Faction) {
        if (user.uuid !in faction.members) {
            faction.members.add(user.uuid)
            user.factionPlayer = FactionPlayer(faction.id, role = PlayerRole.MEMBER)
        }

        updateFactionPower(user, faction)
    }
    fun removePlayerFromFaction(user: User, faction: Faction, kicked: Boolean = false) {
        faction.members.remove(user.uuid)
        user.factionPlayer = null

        if (kicked)
            user.tempJoinBan = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(plugin.settings.get.getInt("Config.tempJoinBanTime").toLong())

        updateFactionPower(faction)
    }

    fun updateFactionPower(faction: Faction) {
        faction.power = 0.0
        faction.members.forEach { updateFactionPower(plugin.userManager.getUserAccountByUUID(it)!!, faction) }
    }
    private fun updateFactionPower(user: User, faction: Faction) {
        faction.power = min(faction.power + plugin.settings.get.getDouble("Config.power.powerPerPlayer"), faction.maxPower)
    }

}