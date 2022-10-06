package phss.factions.bukkit.commands.resolvers.claim

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.types.ClaimResultType

class ClaimCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_CONTROL)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return
        val user = userManager.getUserAccountByUUID(player.uniqueId) ?: return

        when (claimManager.claimChunk(faction, user, player.location.chunk)) {
            ClaimResultType.ERROR_ALREADY_CLAIMED -> {
                val claimFactionOwner = claimManager.getFactionByChunk(player.location.chunk)!!
                if (claimFactionOwner.power > claimFactionOwner.claims.size) {
                    sender.sendMessage(messages.getMessage("chunkAlreadyClaimed"))
                    return
                }

                claimManager.unClaimChunk(claimFactionOwner, player.location.chunk)
                invoke(sender, args)
            }
            ClaimResultType.ERROR_NO_POWER -> sender.sendMessage(messages.getMessage("notHasEnoughPowerToClaim"))
            else -> {
                sender.sendMessage(messages.getMessage("chunkClaimed"))
                factionManager.saveFaction(faction)
            }
        }
    }

}