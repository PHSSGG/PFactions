package phss.factions.bukkit.commands.resolvers

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.FactionPlayer
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.faction.permission.PlayerRole

class CreateCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.NOT_IN_FACTION, CommandRequirement.NEW_FACTION)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val user = userManager.getUserAccountByUUID(player.uniqueId) ?: userManager.createUser(player)
        val faction = factionManager.createFaction(player, args[1])

        faction.members.add(user.uuid)
        user.factionPlayer = FactionPlayer(faction.id, role = PlayerRole.EXILE)

        factionController.addPlayerToFaction(user, faction)

        factionManager.saveFaction(faction)
        userManager.saveUser(user)

        player.sendMessage(messages.getMessage("factionCreated").replaceFactionInfo(faction, plugin))
    }

}