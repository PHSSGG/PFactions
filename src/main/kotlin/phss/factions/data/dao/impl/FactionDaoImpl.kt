package phss.factions.data.dao.impl

import com.google.gson.Gson
import phss.factions.data.dao.DataDao
import phss.factions.data.domain.Faction
import phss.factions.data.domain.GenericData
import phss.factions.database.DatabaseManager
import phss.factions.database.DatabaseManager.Companion.FACTIONS_TABLE
import phss.factions.database.process.task.DataProcessTask

class FactionDaoImpl(
    private val databaseManager: DatabaseManager
) : DataDao<Int, Faction> {

    private val factions = HashMap<Int, Faction>()

    override fun load(): HashMap<Int, Faction> {
        factions.putAll(databaseManager.controller.loadFactions(this))
        return factions
    }

    override val data: HashMap<Int, Faction>
        get() = factions

    override fun create(data: Faction) {
        factions[data.id] = data
        save(data)
    }

    override fun delete(data: Faction) {
        factions.remove(data.id)

        databaseManager.process.task.queue.add((data as GenericData<Any, Any>) to false)
        DataProcessTask.startTask(databaseManager.process.task)
    }

    override fun save(data: Faction) {
        data.dao = this
        factions[data.id] = data

        databaseManager.process.task.queue.add((data as GenericData<Any, Any>) to true)
        DataProcessTask.startTask(databaseManager.process.task)
    }

    override fun saveAll() {
        DataProcessTask.stopTask(databaseManager.process.task)
        factions.values.forEach { databaseManager.controller.saveData(it as GenericData<Any, Any>) }
    }

    override fun getSQLQuery(data: Faction): String {
        return "SELECT * FROM $FACTIONS_TABLE WHERE id='${data.id}'"
    }

    override fun getSQLInsert(data: Faction): String {
        val gson = Gson()
        return StringBuilder()
            .append("INSERT INTO $FACTIONS_TABLE (id, name, leader, display, motd, home, power, max_power, member_cap, type, boss, members, bans, claims, relations, wars, created_at) VALUES (")
            .append("'${data.id}', '${data.name}', '${data.leader}', '${data.display}', '${data.motd}', ")
            .append("'${if (data.home == null) "null" else with(data.home!!) { "${world?.name ?: "null"};$x;$y;$z;$yaw;$pitch" }}', ")
            .append("'${data.power}', '${data.maxPower}', '${data.memberCap}', '${if (data.type == null) "null" else data.type}', '${if (data.boss == null) "null" else data.boss}', ")
            .append("'${gson.toJson(data.members)}', '${gson.toJson(data.bans)}', '${gson.toJson(data.claims)}', '${gson.toJson(data.relations)}', '${gson.toJson(data.wars)}', ")
            .append("'${data.createdAt}'")
            .append(")")
            .toString()
    }

    override fun getSQLUpdate(data: Faction): String {
        val gson = Gson()
        return StringBuilder()
            .append("UPDATE $FACTIONS_TABLE SET ")
            .append("name='${data.name}', leader='${data.leader}', display='${data.display}', motd='${data.motd}', ")
            .append("home='${if (data.home == null) "null" else with(data.home!!) { "${world?.name ?: "null"};$x;$y;$z;$yaw;$pitch" }}', ")
            .append("power='${data.power}', max_power='${data.maxPower}', member_cap='${data.memberCap}', type='${if (data.type == null) "null" else data.type}', boss='${if (data.boss == null) "null" else data.boss}', ")
            .append("members='${gson.toJson(data.members)}', bans='${gson.toJson(data.bans)}', claims='${gson.toJson(data.claims)}', relations='${gson.toJson(data.relations)}', wars='${gson.toJson(data.wars)}', ")
            .append("created_at='${data.createdAt}' ")
            .append("WHERE id='${data.id}'")
            .toString()
    }

    override fun getSQLDelete(data: Faction): String {
        return "DELETE FROM $FACTIONS_TABLE WHERE id='${data.id}'"
    }

}