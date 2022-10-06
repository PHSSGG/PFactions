package phss.factions.faction

import phss.factions.faction.permission.PlayerRole
import java.io.Serializable

class FactionPlayer(
    val factionId: Int,
    var role: PlayerRole = PlayerRole.DEFAULT,
    val joinedAt: Long = System.currentTimeMillis()
) : Serializable