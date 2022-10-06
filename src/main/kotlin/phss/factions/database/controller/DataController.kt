package phss.factions.database.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import org.bukkit.Location
import phss.factions.data.dao.DataDao
import phss.factions.data.domain.Faction
import phss.factions.data.domain.GenericData
import phss.factions.data.domain.User
import phss.factions.database.Database
import phss.factions.database.DatabaseManager
import phss.factions.faction.*
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import kotlin.collections.HashMap

class DataController(
    private val database: Database
) {

    fun loadFactions(dataDao: DataDao<Int, Faction>): HashMap<Int, Faction> {
        val factions = HashMap<Int, Faction>()
        database.open()

        val gson = Gson()
        val resultSet = database.statement?.executeQuery("SELECT * FROM ${DatabaseManager.FACTIONS_TABLE}") ?: return factions
        while (resultSet.next()) { with(resultSet) {
            val id = getInt("id")
            val name = getString("name")
            val leader = UUID.fromString(getString("leader"))
            val display = getString("display")
            val motd = getString("motd")
            val home: Location? = with(getString("home")) {
                if (this == "null") null else {
                    val input = this.split(";")
                    Location(Bukkit.getWorld(input[0]), input[1].toDouble(), input[2].toDouble(), input[3].toDouble(), input[4].toFloat(), input[5].toFloat())
                }
            }
            val power = getDouble("power")
            val maxPower = getDouble("max_power")
            val memberCap = getInt("member_cap")
            val type = getString("type")
            val boss = getString("boss")
            val members = getString("members")
            val bans = getString("bans")
            val claims = getString("claims")
            val relations = getString("relations")
            val wars = getString("wars")
            val createdAt = getLong("created_at")

            factions[id] = Faction(
                id, name, leader, display, motd, home,
                power, maxPower, memberCap,
                if (type == "null") null else type, if (boss == "null") null else boss,
                gson.fromJson(members, object : TypeToken<ArrayList<UUID>>() {}.type),
                gson.fromJson(bans, object : TypeToken<ArrayList<FactionBan>>() {}.type),
                gson.fromJson(claims, object : TypeToken<ArrayList<FactionLocation>>() {}.type),
                gson.fromJson(relations, object : TypeToken<ArrayList<FactionRelation>>() {}.type),
                gson.fromJson(wars, object : TypeToken<ArrayList<FactionWar>>() {}.type),
                createdAt
            ).also { it.dao = dataDao }
        } }
        resultSet.close()

        database.close()
        return factions
    }

    fun loadUsers(dataDao: DataDao<UUID, User>): HashMap<UUID, User> {
        val accounts = HashMap<UUID, User>()
        database.open()

        val gson = Gson()
        val resultSet = database.statement?.executeQuery("SELECT * FROM ${DatabaseManager.USERS_TABLE}") ?: return accounts
        while (resultSet.next()) { with(resultSet) {
            val uuid = UUID.fromString(getString("uuid"))
            val name = getString("name")
            val factionPlayerString = getString("faction_player")
            val maxPower = getDouble("max_power")
            val power = getDouble("power")
            val kills = getInt("kills")
            val deaths = getInt("deaths")
            val tempJoinBan = getLong("temp_join_ban")
            val lastSeen = getLong("last_seen")

            accounts[uuid] = User(
                uuid, name,
                if (factionPlayerString == "null") null else gson.fromJson<FactionPlayer>(factionPlayerString, object : TypeToken<FactionPlayer>() {}.type),
                maxPower,
                power,
                kills, deaths,
                if (tempJoinBan == 0L) null else tempJoinBan,
                lastSeen
            ).also { it.dao = dataDao }
        } }
        resultSet.close()

        database.close()
        return accounts
    }

    fun saveData(data: GenericData<Any, Any>) {
        database.open()
        
        val statement: Statement = database.statement!!
        try {
            val resultSet = statement.executeQuery(data.dao.getSQLQuery(data))
            if (resultSet.next()) statement.executeUpdate(data.dao.getSQLUpdate(data))
            else statement.execute(data.dao.getSQLInsert(data))
            resultSet.close()
        } catch (throwable: SQLException) {
            throwable.printStackTrace()
        }
        
        database.close()
    }

    fun deleteData(data: GenericData<Any, Any>) {
        database.open()
        
        try {
            database.statement!!.execute(data.dao.getSQLDelete(data))
        } catch (throwable: SQLException) {
            throwable.printStackTrace()
        }

        database.close()
    }

}