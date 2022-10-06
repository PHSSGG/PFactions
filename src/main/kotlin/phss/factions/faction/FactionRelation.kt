package phss.factions.faction

import phss.factions.faction.types.RelationType
import java.io.Serializable
import java.util.*

class FactionRelation(
    val author: UUID,
    val factionId: Int,
    val type: RelationType,
    val appliedTime: Long = System.currentTimeMillis()
) : Serializable