package phss.factions.bukkit.commands.resolvers.chat

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.getFaction
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.faction.types.RelationType

class AllyChatCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.ANY)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val faction = (sender as Player).getFaction(plugin) ?: return
        val receivers = ArrayList(faction.members)
        for (relation in faction.relations.filter { it.type == RelationType.ALLY }) {
            plugin.factionManager.getFactionById(relation.factionId)?.members?.run {
                receivers.addAll(this)
            }
        }

        var message = ""
        (1 until args.size).forEach { i ->
            message = "$message${args[i]} "
        }

        val format = messages.getMessage("chatFormat.allychat").replaceFactionInfo(faction, plugin).replace("{player}", sender.name).replace("{message}", message)
        receivers.forEach { Bukkit.getPlayer(it)?.sendMessage(format) }
    }

}