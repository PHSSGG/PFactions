package phss.factions.bukkit.commands.resolvers.relation.ally

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement

class AllyAcceptCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_CONTROL)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val playerFaction = factionManager.getFactionByPlayer((sender as Player).uniqueId) ?: return
        val targetFaction = factionManager.getFactionByName(args[1]) ?: return
        val request = requestService.getRequest(playerFaction, targetFaction.name)

        if (request == null) {
            sender.sendMessage(messages.getMessage("allyNotInvited"))
            return
        }

        request.receiver = sender.name to sender.uniqueId
        request.accepted = true
        requestService.removeRequest(request)
    }

}