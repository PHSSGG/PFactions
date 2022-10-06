package phss.factions.data.domain

import org.bukkit.Location
import phss.factions.data.dao.DataDao
import phss.factions.faction.FactionBan
import phss.factions.faction.FactionLocation
import phss.factions.faction.FactionRelation
import phss.factions.faction.FactionWar
import java.util.*
import kotlin.collections.ArrayList

data class Faction(
    val id: Int,
    val name: String,
    var leader: UUID,
    var display: String = name,
    var motd: String = "",
    var home: Location? = null,
    var power: Double = 0.0,
    var maxPower: Double = 10.0,
    var memberCap: Int = 10,
    var type: String? = null,
    var boss: String? = null,
    val members: ArrayList<UUID> = ArrayList(),
    val bans: ArrayList<FactionBan> = ArrayList(),
    val claims: ArrayList<FactionLocation> = ArrayList(),
    val relations: ArrayList<FactionRelation> = ArrayList(),
    val wars: ArrayList<FactionWar> = ArrayList(),
    val createdAt: Long = System.currentTimeMillis()
) : GenericData<Int, Faction> {

    private lateinit var dataDao: DataDao<Int, Faction>

    override val data: Faction
        get() = this

    override var dao: DataDao<Int, Faction>
        get() = dataDao
        set(value) {
            dataDao = value
        }

}