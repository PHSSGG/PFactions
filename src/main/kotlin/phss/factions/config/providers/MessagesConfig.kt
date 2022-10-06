package phss.factions.config.providers

import phss.factions.PFactions
import phss.factions.utils.extensions.replaceColor

class MessagesConfig(
    private val plugin: PFactions
) {

    fun getMessage(message: String) = plugin.lang.get.getString("Messages.$message")?.replaceColor() ?: "Cannot get '$message' message"
    fun getMessageList(message: String) = plugin.lang.get.getStringList("Messages.$message").replaceColor()

}