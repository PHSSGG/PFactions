package phss.factions.bukkit.commands.resolvers.invite

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement

class InviteDenyCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.NOT_IN_FACTION, CommandRequirement.ANOTHER_PLAYER)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val inviteSender = Bukkit.getPlayer(args[1]) ?: return
        val request = requestService.getRequest(player, inviteSender.name)

        if (request == null) {
            sender.sendMessage(messages.getMessage("factionNotInvited"))
            return
        }

        request.accepted = false
        requestService.removeRequest(request)
    }

}