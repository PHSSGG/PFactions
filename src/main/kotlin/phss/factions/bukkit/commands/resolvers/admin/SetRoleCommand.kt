package phss.factions.bukkit.commands.resolvers.admin

import org.bukkit.command.CommandSender
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.permission.PlayerRole

class SetRoleCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_ADMIN, CommandRequirement.ANOTHER_PLAYER, CommandRequirement.ANOTHER_IN_ANY_FACTION)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        if (args.size != 3) {
            sender.sendMessage(messages.getMessage("help.setrole"))
            return
        }

        val targetPlayer = userManager.getUserAccountByName(args[1])!!
        val role = PlayerRole.values().find { it.name.equals(args[2], ignoreCase = true) }

        if (role == null) {
            sender.sendMessage(messages.getMessage("admin.roleNotFound"))
            return
        }

        targetPlayer.factionPlayer!!.role = role
        userManager.saveUser(targetPlayer)

        sender.sendMessage(messages.getMessage("admin.playerRoleChanged").replace("{player}", targetPlayer.name).replace("{role}", role.toString()))
    }

}