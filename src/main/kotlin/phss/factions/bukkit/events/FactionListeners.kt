package phss.factions.bukkit.events

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import phss.factions.PFactions
import phss.factions.faction.extensions.getFaction
import phss.factions.faction.extensions.hasSafeRelation
import phss.factions.faction.extensions.replaceFactionInfo

class FactionListeners(
    private val plugin: PFactions
) : Listener {

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (event.entity.killer != null) {
            val victimFaction = event.entity.getFaction(plugin) ?: return
            val killerFaction = event.entity.killer!!.getFaction(plugin) ?: return

            if (victimFaction.wars.any { it.factionId == killerFaction.id } && (victimFaction.leader == event.entity.uniqueId)) {
                val killerWarModel = killerFaction.wars.find { it.factionId == victimFaction.id } ?: return
                killerWarModel.otherFactionLeaderDeaths += 1

                if (killerWarModel.otherFactionLeaderDeaths == 3) {
                    val victimWarModel = victimFaction.wars.find { it.factionId == killerFaction.id }!!

                    victimFaction.members.forEach { Bukkit.getPlayer(it)?.sendMessage(plugin.messages.getMessage("warLose").replaceFactionInfo(killerFaction, plugin)) }
                    killerFaction.members.forEach { Bukkit.getPlayer(it)?.sendMessage(plugin.messages.getMessage("warWin").replaceFactionInfo(victimFaction, plugin)) }

                    victimFaction.wars.remove(victimWarModel)
                    killerFaction.wars.remove(killerWarModel)

                    plugin.factionManager.saveFaction(victimFaction, killerFaction)
                }
            }
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Player) {
            val entityFaction = (event.entity as Player).getFaction(plugin) ?: return
            val damagerFaction = (event.damager as Player).getFaction(plugin) ?: return

            if (entityFaction.hasSafeRelation(damagerFaction)) {
                event.isCancelled = true
                return
            }
        }
    }

    /**
     * CLAIM EVENTS
     */

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.to != null && event.from != event.to) {
            if (plugin.claimManager.isChunkClaimed(event.to!!.chunk)) {
                val chunkFactionOwner = plugin.claimManager.getFactionByChunk(event.to!!.chunk)!!
                if (plugin.claimManager.getFactionByChunk(event.from.chunk) == chunkFactionOwner) return

                event.player.sendTitle(
                    plugin.messages.getMessage("claimEnter.title").replaceFactionInfo(chunkFactionOwner, plugin),
                    plugin.messages.getMessage("claimEnter.subtitle").replaceFactionInfo(chunkFactionOwner, plugin),
                    10, 40, 20
                )
            } else if (plugin.claimManager.isChunkClaimed(event.from.chunk)) {
                if (plugin.claimManager.getFactionByChunk(event.from.chunk) == plugin.claimManager.getFactionByChunk(event.to!!.chunk)) return

                val chunkFactionOwner = plugin.claimManager.getFactionByChunk(event.from.chunk)!!
                event.player.sendTitle(
                    plugin.messages.getMessage("claimLeave.title").replaceFactionInfo(chunkFactionOwner, plugin),
                    plugin.messages.getMessage("claimLeave.subtitle").replaceFactionInfo(chunkFactionOwner, plugin),
                    10, 40, 20
                )
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!checkIfCanInteract(event.player, event.block.location.chunk)) event.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!checkIfCanInteract(event.player, event.block.location.chunk)) event.isCancelled = true
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.hasBlock()) {
            if (!checkIfCanInteract(event.player, event.clickedBlock!!.location.chunk)) event.isCancelled = true
            return
        }
        if (!checkIfCanInteract(event.player, event.player.location.chunk)) event.isCancelled = true
    }

    private fun checkIfCanInteract(player: Player, chunk: Chunk): Boolean {
        if (plugin.claimManager.isChunkClaimed(chunk)) {
            val faction = plugin.claimManager.getFactionByChunk(chunk)!!
            val userAccount = plugin.userManager.getUserAccountByUUID(player.uniqueId)

            if (userAccount?.factionPlayer?.factionId?.equals(faction.id) != true) return false
        }

        return true
    }

}