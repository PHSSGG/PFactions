package phss.factions.managers

import org.bukkit.Chunk
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import phss.factions.PFactions
import phss.factions.data.domain.Faction
import phss.factions.data.domain.User
import phss.factions.faction.FactionLocation
import phss.factions.faction.types.ClaimResultType

class ClaimManager(
    val plugin: PFactions
) {

    val chunkClaimedKey = NamespacedKey(plugin, "faction_chunk")

    fun claimChunk(faction: Faction, user: User, chunk: Chunk, forced: Boolean = false): ClaimResultType {
        if (!forced) {
            if (faction.claims.size >= faction.power) return ClaimResultType.ERROR_NO_POWER
            if (isChunkClaimed(chunk)) return ClaimResultType.ERROR_ALREADY_CLAIMED
        }

        chunk.persistentDataContainer.set(chunkClaimedKey, PersistentDataType.STRING, "${faction.id}")
        faction.claims.add(FactionLocation(chunk.world.name, chunk.x, chunk.z, faction.id, user.uuid))

        return ClaimResultType.SUCCESS_CHUNK_CLAIMED
    }

    fun unClaimChunk(faction: Faction, chunk: Chunk): ClaimResultType {
        val claimOwner = getFactionByChunk(chunk)
        if (claimOwner == null || claimOwner.id != faction.id) return ClaimResultType.ERROR_NOT_CLAIMED

        chunk.persistentDataContainer.remove(chunkClaimedKey)
        faction.claims.removeIf { it.factionId == faction.id && it.world == chunk.world.name && it.x == chunk.x && it.z == chunk.z }

        return ClaimResultType.SUCCESS_CHUNK_UNCLAIM
    }

    fun isChunkClaimed(chunk: Chunk): Boolean {
        return chunk.persistentDataContainer.has(chunkClaimedKey, PersistentDataType.STRING)
    }
    fun getFactionIdFromChunkData(chunk: Chunk): Int {
        return chunk.persistentDataContainer[chunkClaimedKey, PersistentDataType.STRING]!!.toInt()
    }

    fun getFactionByChunk(chunk: Chunk): Faction? {
        if (!isChunkClaimed(chunk)) return null

        val factionId = getFactionIdFromChunkData(chunk)
        return plugin.factionManager.getFactionById(factionId)
    }

}