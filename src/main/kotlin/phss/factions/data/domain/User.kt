package phss.factions.data.domain

import phss.factions.data.dao.DataDao
import phss.factions.faction.FactionPlayer
import java.util.*

data class User(
    val uuid: UUID,
    var name: String,
    var factionPlayer: FactionPlayer? = null,
    var maxPower: Double = 10.0,
    var power: Double = 0.0,
    var kills: Int = 0,
    var deaths: Int = 0,
    var tempJoinBan: Long? = null,
    var lastSeen: Long = System.currentTimeMillis()
) : GenericData<UUID, User> {

    private lateinit var dataDao: DataDao<UUID, User>

    override val data: User
        get() = this

    override var dao: DataDao<UUID, User>
        get() = dataDao
        set(value) {
            dataDao = value
        }

}