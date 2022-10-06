package phss.factions.bukkit.commands.resolvers.relation.hostile

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.FactionRelation
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.faction.types.RelationType

class HostileCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION,
            CommandRequirement.PERMISSION_LEVEL_CONTROL, CommandRequirement.ANOTHER_FACTION
        )
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val playerFaction = factionManager.getFactionByPlayer((sender as Player).uniqueId) ?: return
        val targetFaction = factionManager.getFactionByName(args[1]) ?: return
        val currentRelation = playerFaction.relations.find { it.factionId == targetFaction.id }

        if (currentRelation != null && currentRelation.type == RelationType.HOSTILE) {
            sender.sendMessage(messages.getMessage("alreadyHostile"))
            return
        }

        playerFaction.relations.remove(currentRelation)
        targetFaction.relations.remove(targetFaction.relations.find { it.factionId == playerFaction.id })

        // Hostile relation model for player faction
        playerFaction.relations.add(FactionRelation(sender.uniqueId, targetFaction.id, RelationType.HOSTILE))
        // Hostile relation model for target faction
        targetFaction.relations.add(FactionRelation(sender.uniqueId, playerFaction.id, RelationType.HOSTILE))

        factionManager.saveFaction(playerFaction, targetFaction)

        targetFaction.members.filter { Bukkit.getPlayer(it) != null && userManager.getUserAccountByUUID(it)?.factionPlayer?.role?.permissionLevel ?: 0 >= 2 }.forEach {
            Bukkit.getPlayer(it)?.sendMessage(messages.getMessage("hostileAddedAlert").replace("{author}", sender.name).replaceFactionInfo(targetFaction, plugin))
        }

        sender.sendMessage(messages.getMessage("hostileAdded").replaceFactionInfo(targetFaction, plugin))
    }

}