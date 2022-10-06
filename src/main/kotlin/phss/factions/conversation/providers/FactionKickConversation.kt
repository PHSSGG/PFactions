package phss.factions.conversation.providers

import org.bukkit.Bukkit
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.resolvers.KickCommand

class FactionKickConversation : StringPrompt() {

    companion object {
        const val PLAYER_KEY = "player"
    }

    override fun getPromptText(context: ConversationContext): String {
        return (context.plugin as PFactions?)?.messages?.getMessage("conversation.kick") ?: ""
    }

    override fun acceptInput(context: ConversationContext, name: String?): Prompt? {
        val plugin = context.plugin as PFactions?
        val player = context.forWhom as Player
        val playerName = (name ?: context.getSessionData(PLAYER_KEY) as String?).also {
            context.setSessionData(PLAYER_KEY, null)
        }

        if (plugin == null || playerName == null) return this

        Bukkit.getScheduler().callSyncMethod(plugin) {
            KickCommand(plugin).execute(player, "kick", arrayOf("kick", playerName))
        }
        return END_OF_CONVERSATION
    }

    override fun blocksForInput(context: ConversationContext): Boolean {
        return context.getSessionData(PLAYER_KEY) == null
    }

}