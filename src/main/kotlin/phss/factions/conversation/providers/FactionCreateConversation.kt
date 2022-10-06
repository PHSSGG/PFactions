package phss.factions.conversation.providers

import org.bukkit.Bukkit
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.jetbrains.annotations.Nullable
import phss.factions.PFactions
import phss.factions.bukkit.commands.resolvers.CreateCommand

class FactionCreateConversation : StringPrompt() {

    companion object {
        const val NAME_KEY = "name"
    }

    override fun getPromptText(context: ConversationContext): String {
        return (context.plugin as PFactions?)?.messages?.getMessage("conversation.create") ?: ""
    }

    @Nullable
    override fun acceptInput(context: ConversationContext, name: String?): Prompt? {
        val plugin = context.plugin as PFactions?
        val player = context.forWhom as Player
        val factionName = (name ?: context.getSessionData(NAME_KEY) as String?).also {
            context.setSessionData(NAME_KEY, null)
        }
        if (plugin == null || factionName == null) return this

        Bukkit.getScheduler().callSyncMethod(plugin) {
            CreateCommand(plugin).execute(Bukkit.getPlayer(player.uniqueId)!!, "create", arrayOf("create", factionName))
        }
        return END_OF_CONVERSATION
    }

    override fun blocksForInput(context: ConversationContext): Boolean {
        return context.getSessionData(NAME_KEY) == null
    }

}