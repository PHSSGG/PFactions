package phss.factions.bukkit.commands.resolvers

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement

class PromoteCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_CONTROL,
            CommandRequirement.ANOTHER_PLAYER, CommandRequirement.ANOTHER_IN_SAME_FACTION
        )
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val senderAccount = userManager.getUserAccountByUUID((sender as Player).uniqueId) ?: return
        val target = userManager.getUserAccountByName(args[1]) ?: return
        val targetFactionPlayer = target.factionPlayer!!

        if (targetFactionPlayer.role.isMax()) {
            sender.sendMessage(messages.getMessage("targetAlreadyInMaxRole"))
            return
        }
        if (senderAccount.factionPlayer!!.role.permissionLevel >= targetFactionPlayer.role.permissionLevel) {
            sender.sendMessage(messages.getMessage("cannotPromoteSameOrHigher"))
            return
        }
        targetFactionPlayer.role = targetFactionPlayer.role.getNextRole()

        userManager.saveUser(target)

        sender.sendMessage(messages.getMessage("targetPromote").replace("{player}", target.name).replace("{role}", targetFactionPlayer.role.toString()))
        Bukkit.getPlayer(target.uuid)?.sendMessage(messages.getMessage("rolePromoted").replace("{author}", sender.name).replace("{role}", targetFactionPlayer.role.toString()))
    }

}