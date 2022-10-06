package phss.factions.bukkit.commands.resolvers.admin

import org.bukkit.command.CommandSender
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.replaceFactionInfo

class GiveBossCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_ADMIN, CommandRequirement.ANOTHER_FACTION)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val targetFaction = factionManager.getFactionByName(args[1])!!

        if (targetFaction.type == null) {
            sender.sendMessage("admin.factionTypeNotSelected")
            return
        }
        if (targetFaction.boss != null) {
            sender.sendMessage("admin.factionAlreadyHasBoss")
            return
        }

        targetFaction.boss = targetFaction.type
        factionManager.saveFaction(targetFaction)

        sender.sendMessage(messages.getMessage("admin.bossGived").replaceFactionInfo(targetFaction, plugin))
    }

}