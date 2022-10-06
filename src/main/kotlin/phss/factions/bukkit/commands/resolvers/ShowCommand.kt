package phss.factions.bukkit.commands.resolvers

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.bukkit.view.getSortedFactions
import phss.factions.bukkit.view.openFactionInfoMenu
import phss.factions.data.domain.Faction
import java.lang.NumberFormatException

class ShowCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        var desiredFaction: Faction? = null

        if (args.size == 1) desiredFaction = claimManager.getFactionByChunk(player.location.chunk)
        else {
            try {
                // try to get a faction by top position
                val position = Integer.parseInt(args[1])
                desiredFaction = factionManager.getFactions().getSortedFactions(0).getOrNull(position - 1)
            } catch (ignored: NumberFormatException) {
                // try to get a faction by a member name
                val target = userManager.getUserAccountByName(args[1])
                if (target?.factionPlayer != null) desiredFaction = factionManager.getFactionById(target.factionPlayer!!.factionId)

                // try to get a faction by name
                if (desiredFaction == null) desiredFaction = factionManager.getFactionByName(args[1])
            }
        }

        if (desiredFaction != null) player.openFactionInfoMenu(plugin, desiredFaction)
        else player.sendMessage(messages.getMessage("noFactionHaveBeenFound"))
    }

}