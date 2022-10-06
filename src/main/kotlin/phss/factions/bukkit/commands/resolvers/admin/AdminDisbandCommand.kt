package phss.factions.bukkit.commands.resolvers.admin

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.replaceFactionInfo

class AdminDisbandCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_ADMIN, CommandRequirement.ANOTHER_FACTION)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val targetFaction = factionManager.getFactionByName(args[1])!!

        targetFaction.members.forEach {
            val target = userManager.getUserAccountByUUID(it)
            if (target != null) {
                target.factionPlayer = null
                userManager.saveUser(target)

                Bukkit.getPlayer(it)?.sendMessage(messages.getMessage("factionDisband").replaceFactionInfo(targetFaction, plugin))
            }
        }

        sender.sendMessage(messages.getMessage("admin.factionDisband").replaceFactionInfo(targetFaction, plugin))
        factionManager.disbandFaction(targetFaction)
    }

}