package phss.factions.faction.extensions

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.data.domain.Faction
import phss.factions.faction.FactionWarProposal
import phss.factions.faction.types.RelationType
import java.util.*

fun Faction.isPlayerBanned(uuid: UUID) = bans.any { it.banned == uuid }

fun FactionWarProposal.getFactionByPlayer(player: Player): Faction {
    return if (factionOne.leader == player.uniqueId) factionOne else factionTwo
}

fun LivingEntity.getFaction(plugin: PFactions): Faction? {
    return plugin.factionManager.getFactionByPlayer(uniqueId)
}

fun Faction.hasSafeRelation(other: Faction): Boolean {
    return relations.any { (it.type == RelationType.ALLY || it.type == RelationType.OUT_OF_WAR) && it.factionId == other.id }
}