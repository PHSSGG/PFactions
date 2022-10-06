package phss.factions.bukkit.commands.resolvers.admin

import org.bukkit.command.CommandSender
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.replaceFactionInfo

class SetPlayerCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_ADMIN, CommandRequirement.ANOTHER_FACTION_AND_PLAYER)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val targetFaction = factionManager.getFactionByName(args[1])!!
        val targetPlayer = userManager.getUserAccountByName(args[2])!!

        if (targetPlayer.uuid in targetFaction.members) {
            sender.sendMessage(messages.getMessage("admin.playerAlreadyInFaction"))
            return
        }

        if (targetPlayer.factionPlayer != null) {
            val currentFaction = factionManager.getFactionById(targetPlayer.factionPlayer!!.factionId)
            currentFaction?.run {
                if (members.size == 1) factionManager.disbandFaction(currentFaction)
                else {
                    members.remove(targetPlayer.uuid)
                    targetPlayer.factionPlayer = null

                    if (currentFaction.leader == targetPlayer.uuid) currentFaction.leader = members.first()
                    factionManager.saveFaction(currentFaction)
                }
            }
        }

        factionController.addPlayerToFaction(targetPlayer, targetFaction)
        factionManager.saveFaction(targetFaction)
        userManager.saveUser(targetPlayer)

        sender.sendMessage(messages.getMessage("admin.playerFactionChanged").replaceFactionInfo(targetFaction, plugin).replace("{player}", targetPlayer.name))
    }

}