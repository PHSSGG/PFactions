package phss.factions.utils.extensions

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun CommandSender.sendMessage(list: List<String>) {
    for (line in list)
        sendMessage(line)
}
fun String.replaceColor() = ChatColor.translateAlternateColorCodes('&', this)!!
fun List<String>.replaceColor(): List<String> {
    val list = ArrayList<String>()
    for (line in this) list.add(ChatColor.translateAlternateColorCodes('&', line))

    return list
}
fun List<String>.replace(one: Any, two: Any): List<String> {
    val list = ArrayList<String>()
    for (line in this) list.add(line.replace("$one", "$two"))

    return list
}