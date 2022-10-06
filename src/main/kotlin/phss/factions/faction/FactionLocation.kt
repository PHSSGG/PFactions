package phss.factions.faction

import java.io.Serializable
import java.util.*

class FactionLocation(
    val world: String,
    val x: Int,
    val z: Int,
    val factionId: Int,
    var claimedBy: UUID,
    val claimedAt: Long = System.currentTimeMillis()
) : Serializable