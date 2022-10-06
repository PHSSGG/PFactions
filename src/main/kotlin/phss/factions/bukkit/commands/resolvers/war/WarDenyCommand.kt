package phss.factions.bukkit.commands.resolvers.war

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.replaceFactionInfo

class WarDenyCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_LEADER)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val playerFaction = factionManager.getFactionByPlayer((sender as Player).uniqueId) ?: return
        val targetFaction = factionManager.getFactionByName(args[1]) ?: return
        val request = requestService.getRequest(playerFaction, targetFaction.name)

        if (request == null) {
            sender.sendMessage(messages.getMessage("warNotInvited"))
            return
        }

        request.receiver = sender.name to sender.uniqueId
        request.accepted = false
        requestService.removeRequest(request)

        sender.sendMessage(messages.getMessage("warInviteDeny").replaceFactionInfo(targetFaction, plugin))
    }

}