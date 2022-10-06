package phss.factions.bukkit.commands.resolvers

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.FactionBan

class BanCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_CONTROL,
            CommandRequirement.ANOTHER_PLAYER, CommandRequirement.ANOTHER_NOT_BANNED
        )
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return
        val target = userManager.getUserAccountByName(args[1]) ?: return

        val ban = FactionBan(player.uniqueId, target.uuid, System.currentTimeMillis())
        faction.bans.add(ban)

        factionManager.saveFaction(faction)

        if (target.factionPlayer != null && target.factionPlayer!!.factionId == faction.id) {
            factionController.removePlayerFromFaction(target, faction, true)
            userManager.saveUser(target)

            Bukkit.getPlayer(target.uuid)?.sendMessage(messages.getMessage("factionBanAlert").replace("{author}", player.name).replace("{faction}", faction.name))
        }

        player.sendMessage(messages.getMessage("targetBanSuccess").replace("{target}", target.name))
    }

}