package phss.factions.conversation

import org.bukkit.conversations.*
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import kotlin.collections.HashMap

class FactionConversation(
    plugin: Plugin,
    forWhom: Conversable,
    firstPrompt: Prompt?,
    initialSessionData: Map<Any, Any> = HashMap(),
    timeout: Int = 30
) : Conversation(plugin, forWhom, firstPrompt, initialSessionData) {

    companion object {
        val conversations = HashMap<UUID, FactionConversation>()
    }

    override fun begin() {
        val playerUUID = (forWhom as Player).uniqueId
        val oldConversation = conversations[playerUUID]
        if (oldConversation !== this && oldConversation != null) {
            forWhom.abandonConversation(oldConversation)
        }

        conversations[playerUUID] = this
        super.begin()
    }

    fun addCancellation(cancellation: ConversationCanceller): FactionConversation {
        cancellation.setConversation(this)
        cancellers.add(cancellation)

        return this
    }

    init {
        this.isLocalEchoEnabled = true
        addCancellation(ConversationInactivityCancellation(plugin, timeout))
    }

}