package phss.factions.bukkit.commands.resolvers

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.replaceFactionInfo

class LeaveCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val userAccount = userManager.getUserAccountByUUID(player.uniqueId) ?: return
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return

        if (faction.leader == player.uniqueId) {
            player.chat("/f disband")
            return
        }

        factionController.removePlayerFromFaction(userAccount, faction)

        factionManager.saveFaction(faction)
        userManager.saveUser(userAccount)

        player.sendMessage(messages.getMessage("factionLeave").replaceFactionInfo(faction, plugin))
    }

}