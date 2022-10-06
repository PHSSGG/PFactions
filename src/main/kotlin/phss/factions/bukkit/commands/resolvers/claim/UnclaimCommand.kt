package phss.factions.bukkit.commands.resolvers.claim

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.types.ClaimResultType

class UnclaimCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_CONTROL)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return

        when (claimManager.unClaimChunk(faction, player.location.chunk)) {
            ClaimResultType.ERROR_NOT_CLAIMED -> sender.sendMessage(messages.getMessage("chunkNotClaimed"))
            else -> {
                sender.sendMessage(messages.getMessage("chunkUnclaimed"))
                factionManager.saveFaction(faction)
            }
        }
    }

}