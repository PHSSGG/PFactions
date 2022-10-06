package phss.factions.faction

import phss.factions.data.domain.Faction

class FactionWarProposal(
    val factionOne: Faction,
    val factionTwo: Faction,
    val callback: ProposalCallback
) {

    var currentInConversation = factionOne
    var currentDuration = 1

    interface ProposalCallback {
        fun onTimeProposed(newTime: Int)
        fun onFactionAccept(factionId: Int)
        fun onFactionDeny(factionId: Int)
    }

}