package phss.factions.faction

import java.io.Serializable
import java.util.*

class FactionWar(
    val factionId: Int,
    val invitedBy: UUID,
    val acceptedBy: UUID,
    val duration: Long,
    var otherFactionLeaderDeaths: Int = 0,
    val appliedTime: Long = System.currentTimeMillis()
) : Serializable