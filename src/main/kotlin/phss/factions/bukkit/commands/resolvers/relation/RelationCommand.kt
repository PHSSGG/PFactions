package phss.factions.bukkit.commands.resolvers.relation

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.bukkit.view.openFactionRelationsMenu

class RelationCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        (sender as Player).openFactionRelationsMenu(plugin)
    }

}
