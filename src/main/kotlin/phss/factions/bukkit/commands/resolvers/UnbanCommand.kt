package phss.factions.bukkit.commands.resolvers

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement

class UnbanCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_CONTROL,
            CommandRequirement.ANOTHER_PLAYER, CommandRequirement.ANOTHER_BANNED
        )
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return
        val target = userManager.getUserAccountByName(args[1]) ?: return
        val ban = faction.bans.find { it.banned == target.uuid } ?: return

        faction.bans.remove(ban)
        factionManager.saveFaction(faction)

        player.sendMessage(messages.getMessage("targetUnBanSuccess").replace("{target}", target.name))
        Bukkit.getPlayer(target.uuid)?.sendMessage(messages.getMessage("factionUnBanAlert").replace("{author}", player.name).replace("{faction}", faction.name))
    }

}