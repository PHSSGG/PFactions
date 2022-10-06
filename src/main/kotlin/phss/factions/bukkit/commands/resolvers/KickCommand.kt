package phss.factions.bukkit.commands.resolvers

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement

class KickCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_CONTROL,
            CommandRequirement.ANOTHER_PLAYER, CommandRequirement.ANOTHER_IN_SAME_FACTION
        )
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return
        val target = userManager.getUserAccountByName(args[1]) ?: return

        factionController.removePlayerFromFaction(target, faction, true)

        factionManager.saveFaction(faction)
        userManager.saveUser(target)

        player.sendMessage(messages.getMessage("targetKickSuccess").replace("{player}", target.name))
        Bukkit.getPlayer(target.uuid)?.sendMessage(messages.getMessage("factionKick").replace("{author}", target.name).replace("{faction}", faction.name))
    }

}