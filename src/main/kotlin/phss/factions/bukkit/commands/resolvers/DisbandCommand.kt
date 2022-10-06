package phss.factions.bukkit.commands.resolvers

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.replaceFactionInfo

class DisbandCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_LEADER)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return

        faction.members.forEach {
            val target = userManager.getUserAccountByUUID(it)
            if (target != null) {
                target.factionPlayer = null
                userManager.saveUser(target)

                Bukkit.getPlayer(it)?.sendMessage(messages.getMessage("factionDisband").replaceFactionInfo(faction, plugin))
            }
        }

        player.sendMessage(messages.getMessage("factionDisbandSuccess").replaceFactionInfo(faction, plugin))
        factionManager.disbandFaction(faction)
    }

}