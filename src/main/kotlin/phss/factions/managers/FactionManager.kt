package phss.factions.managers

import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.data.domain.Faction
import java.util.*

class FactionManager(
    private val plugin: PFactions
) {

    fun getFactions(): List<Faction> {
        return plugin.factionRepository.data
    }

    fun getFactionById(factionId: Int): Faction? {
        return plugin.factionRepository[factionId]
    }

    fun getFactionByPlayer(playerUUID: UUID): Faction? {
        return plugin.factionRepository.data.find { it.members.contains(playerUUID) }
    }

    fun getFactionByName(factionName: String): Faction? {
        return plugin.factionRepository.data.find { it.name == factionName }
    }

    fun createFaction(leader: Player, factionName: String, save: Boolean = false): Faction {
        val settings = plugin.settings.get
        val faction = Faction(plugin.factionRepository.data.size + 1, factionName, leader.uniqueId,
            maxPower = settings.getDouble("Config.power.maxFactionPower"),
            memberCap = settings.getInt("Config.defaultMemberCap")
        )
        if (save) plugin.factionRepository.create(faction)

        return faction
    }

    fun disbandFaction(faction: Faction) {
        plugin.factionRepository.delete(faction)
    }

    fun saveFaction(vararg factions: Faction) {
        for (faction in factions) saveFaction(faction)
    }
    fun saveFaction(faction: Faction) {
        plugin.factionRepository.edit(faction)
    }

}