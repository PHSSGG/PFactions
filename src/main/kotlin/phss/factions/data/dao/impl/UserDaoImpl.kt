package phss.factions.data.dao.impl

import com.google.gson.Gson
import phss.factions.data.dao.DataDao
import phss.factions.data.domain.GenericData
import phss.factions.data.domain.User
import phss.factions.database.DatabaseManager
import phss.factions.database.DatabaseManager.Companion.USERS_TABLE
import phss.factions.database.process.task.DataProcessTask
import java.util.*
import kotlin.collections.HashMap

class UserDaoImpl(
    private val databaseManager: DatabaseManager
) : DataDao<UUID, User> {

    val users = HashMap<UUID, User>()

    override fun load(): HashMap<UUID, User> {
        users.putAll(databaseManager.controller.loadUsers(this))
        return users
    }

    override val data: HashMap<UUID, User>
        get() = users

    override fun create(data: User) {
        users[data.uuid] = data
        save(data)
    }

    override fun delete(data: User) {
        users.remove(data.uuid)

        databaseManager.process.task.queue.add((data as GenericData<Any, Any>) to false)
        DataProcessTask.startTask(databaseManager.process.task)
    }

    override fun save(data: User) {
        data.dao = this
        users[data.uuid] = data

        databaseManager.process.task.queue.add((data as GenericData<Any, Any>) to true)
        DataProcessTask.startTask(databaseManager.process.task)
    }

    override fun saveAll() {
        DataProcessTask.stopTask(databaseManager.process.task)
        users.values.forEach { databaseManager.controller.saveData(it as GenericData<Any, Any>) }
    }

    override fun getSQLQuery(data: User): String {
        return "SELECT * FROM $USERS_TABLE WHERE uuid='${data.uuid}'"
    }

    override fun getSQLInsert(data: User): String {
        val gson = Gson()
        return StringBuilder()
            .append("INSERT INTO $USERS_TABLE (uuid, name, faction_player, max_power, power, kills, deaths, temp_join_ban, last_seen) VALUES (")
            .append("'${data.uuid}', '${data.name}', ")
            .append("'${if (data.factionPlayer == null) "null" else gson.toJson(data.factionPlayer)}', ")
            .append("'${data.maxPower}', '${data.power}', '${data.kills}', '${data.deaths}', ")
            .append("'${if (data.tempJoinBan == null) 0L else data.tempJoinBan}', ")
            .append("${data.lastSeen}")
            .append(")")
            .toString()
    }

    override fun getSQLUpdate(data: User): String {
        val gson = Gson()

        return StringBuffer()
            .append("UPDATE $USERS_TABLE SET name='${data.name}', ")
            .append("faction_player='${if (data.factionPlayer == null) "null" else gson.toJson(data.factionPlayer)}', ")
            .append("max_power='${data.maxPower}', power='${data.power}', kills='${data.kills}', deaths='${data.deaths}', ")
            .append("temp_join_ban='${if (data.tempJoinBan == null) 0L else data.tempJoinBan}', ")
            .append("last_seen='${data.lastSeen}' ")
            .append("WHERE uuid='${data.uuid}'")
            .toString()
    }

    override fun getSQLDelete(data: User): String {
        return "DELETE FROM $USERS_TABLE WHERE uuid='${data.uuid}'"
    }

}