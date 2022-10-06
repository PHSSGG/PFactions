package phss.factions.faction.extensions

import org.bukkit.Bukkit
import org.bukkit.Location
import phss.factions.PFactions
import phss.factions.data.domain.Faction
import phss.factions.data.domain.User
import phss.factions.utils.convertToTime

fun String.replaceFactionInfo(faction: Faction, plugin: PFactions) = this.replace("{name}", faction.name)
    .replace("{display}", faction.display)
    .replace("{type}", if (faction.type != null) plugin.specialManager.actions[faction.type!!]?.configuration?.display?.name ?: "Not set" else "Not set")
    .replace("{motd}", faction.motd)
    .replace("{home}", faction.home?.toReadable() ?: "Not set")
    .replace("{claims}", "${faction.claims.size}")
    .replace("{maxpower}", "${faction.maxPower}")
    .replace("{power}", "${faction.power}")
    .replace("{members}", "${faction.members.size}")
    .replace("{relations}", "${faction.relations.size}")
    .replace("{wars}", "${faction.wars.size}")
    .replace("{member_cap}", "${faction.memberCap}")
    .replace("{leader}", Bukkit.getOfflinePlayer(faction.leader).name ?: "Null")
    .replace("{bans}", "${faction.bans.size}")
    .replace("{created}", (faction.createdAt - System.currentTimeMillis()).convertToTime(plugin))
fun List<String>.replaceFactionInfo(faction: Faction, plugin: PFactions): List<String> {
    val list = ArrayList<String>()
    for (line in this) list.add(line.replaceFactionInfo(faction, plugin))

    return list
}

fun String.replaceMemberInfo(user: User, plugin: PFactions): String {
    val faction = if (user.factionPlayer != null&& plugin.factionManager.getFactionById(user.factionPlayer!!.factionId) != null) plugin.factionManager.getFactionById(user.factionPlayer!!.factionId)
    else null

    return this.replace("{player}", user.name)
        .replace("{last_seen}", (user.lastSeen - System.currentTimeMillis()).convertToTime(plugin))
        .replace("{kills}", "${user.kills}")
        .replace("{deaths}", "${user.deaths}")
        .replace("{faction}", faction?.name ?: "N/A")
        .replace("{player_power}", "${user.power}")
        .replace("{player_maxpower}", "${user.maxPower}")
        .replace("{role}", user.factionPlayer?.role?.toString() ?: "Member")
}
fun List<String>.replaceMemberInfo(user: User, plugin: PFactions): List<String> {
    val list = ArrayList<String>()
    for (line in this) list.add(line.replaceMemberInfo(user, plugin))

    return list
}

fun Location.toReadable() = "${x.truncate()} - ${y.truncate()} - ${z.truncate()}"
private fun Double.truncate() = String.format("%.2f", this)