package phss.factions.conversation

import org.bukkit.conversations.Conversation
import org.bukkit.conversations.InactivityConversationCanceller
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import phss.factions.PFactions

class ConversationInactivityCancellation(
    plugin: Plugin,
    timeout: Int
) : InactivityConversationCanceller(plugin, timeout) {

    override fun cancelling(conversation: Conversation) {
        (conversation.forWhom as Player).sendMessage((plugin as PFactions).messages.getMessage("conversation.timeout"))
    }

}