package phss.factions.conversation.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import phss.factions.PFactions
import phss.factions.faction.FactionWarProposal
import phss.factions.faction.extensions.getFactionByPlayer
import java.util.*
import kotlin.collections.ArrayList

class FactionWarConversation(
    private val plugin: PFactions
) : Listener {

    val proposals = ArrayList<FactionWarProposal>()

    fun getProposalByFactionId(factionId: Int): FactionWarProposal? {
        return getProposal { factionOne.id == factionId || factionTwo.id == factionId }
    }
    fun getProposalByFactionLeader(leader: UUID): FactionWarProposal? {
        return getProposal { factionOne.leader == leader || factionTwo.leader == leader }
    }

    private fun getProposal(query: FactionWarProposal.() -> Boolean): FactionWarProposal? {
        return proposals.find(query)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onChat(event: AsyncPlayerChatEvent) {
        val proposal = getProposalByFactionLeader(event.player.uniqueId) ?: return
        val faction = proposal.getFactionByPlayer(event.player)
        val otherFaction = if (proposal.currentInConversation == proposal.factionOne) proposal.factionTwo else proposal.factionOne

        if (proposal.currentInConversation != faction) return

        when (event.message.lowercase()) {
            "yes", "y", "accept" -> {
                proposal.callback.onFactionAccept(faction.id)
                proposal.currentInConversation = otherFaction
            }
            "no", "n", "deny" -> proposal.callback.onFactionDeny(faction.id)
            else -> {
                val duration = event.message.toIntOrNull()
                if (duration == null) event.player.sendMessage(plugin.messages.getMessage("onlyNumbers"))
                else {
                    proposal.currentDuration = duration
                    proposal.callback.onTimeProposed(duration)
                }
            }
        }

        event.isCancelled = true
    }

}