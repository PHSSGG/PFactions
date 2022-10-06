package phss.factions.conversation.providers

import org.bukkit.Bukkit
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.faction.special.entity.traits.BossTrait

class FactionBossControlConversation (
    private val trait: BossTrait,
    private val attack: Boolean, // is add attack conversation
    private val friend: Boolean = true // is add friend conversation | if false, it'll be to remove friend
): StringPrompt() {

    companion object {
        const val PLAYER_KEY = "player"
    }

    override fun getPromptText(context: ConversationContext): String {
        return (context.plugin as PFactions?)?.messages?.getMessage("conversation.${when {
            attack -> "bossAttack"
            friend -> "bossFriendAdd"
            else -> "bossFriendRemove"
        }}") ?: ""
    }

    override fun acceptInput(context: ConversationContext, name: String?): Prompt? {
        val plugin = context.plugin as PFactions?
        val player = context.forWhom as Player
        val playerName = (name ?: context.getSessionData(PLAYER_KEY) as String?).also {
            context.setSessionData(PLAYER_KEY, null)
        }

        if (plugin == null || playerName == null || Bukkit.getPlayer(playerName) == null) return this

        Bukkit.getScheduler().callSyncMethod(plugin) {
            if (attack) {
                if (trait.forceAttack?.name == playerName) {
                    trait.forceAttack = null
                    player.sendMessage(plugin.messages.getMessage("control.attackRemoved"))
                    return@callSyncMethod
                }

                trait.forceAttack = Bukkit.getPlayer(playerName)!!
                player.sendMessage(plugin.messages.getMessage("control.attack"))
            } else {
                if (friend) {
                    if (playerName in trait.friend) {
                        player.sendMessage(plugin.messages.getMessage("control.alreadyFriend"))
                        return@callSyncMethod
                    }

                    trait.friend.add(playerName)
                    player.sendMessage(plugin.messages.getMessage("control.friendAdded"))
                } else {
                    if (playerName !in trait.friend) {
                        player.sendMessage(plugin.messages.getMessage("control.alreadyNotFriend"))
                        return@callSyncMethod
                    }

                    trait.friend.remove(playerName)
                    player.sendMessage(plugin.messages.getMessage("control.friendRemoved"))
                }
            }
        }
        return END_OF_CONVERSATION
    }

    override fun blocksForInput(context: ConversationContext): Boolean {
        return context.getSessionData(PLAYER_KEY) == null
    }

}