package phss.factions.faction

import java.io.Serializable
import java.util.*

class FactionBan(
    val author: UUID,
    val banned: UUID,
    val appliedTime: Long = System.currentTimeMillis()
) : Serializable