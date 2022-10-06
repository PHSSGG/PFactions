package phss.factions.bukkit.commands.resolvers.home

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement

class HomeCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return

        if (faction.home == null) {
            sender.sendMessage(messages.getMessage("homeNotSet"))
            return
        }

        player.teleport(faction.home!!)
        sender.sendMessage(messages.getMessage("homeTeleport"))
    }

}