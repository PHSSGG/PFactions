package phss.factions.utils

import phss.factions.PFactions

fun Long.convertToTime(plugin: PFactions): String {
    val seconds = ((this / 1000L) % 60L).toString().replace("-".toRegex(), "")
    val minutes = ((this / 60000L) % 60L).toString().replace("-".toRegex(), "")
    val hours = ((this / 3600000L) % 24L).toString().replace("-".toRegex(), "")
    val days = (this / (60*60*24*1000)).toString().replace("-".toRegex(), "")

    val settings = plugin.settings.get
    val secondsFormat = settings.getString("Config.timeFormat.seconds")
    val minutesFormat = settings.getString("Config.timeFormat.minutes")
    val hoursFormat = settings.getString("Config.timeFormat.hours")
    val daysFormat = settings.getString("Config.timeFormat.days")

    if (days == "0" && hours == "0" && minutes == "0") return "${seconds}$secondsFormat"
    if (days == "0" && hours == "0") return "${minutes}$minutesFormat"

    return if (days == "0") "${hours}$hoursFormat ${minutes}${minutesFormat}"
    else "${days}$daysFormat ${hours}$hoursFormat"
}