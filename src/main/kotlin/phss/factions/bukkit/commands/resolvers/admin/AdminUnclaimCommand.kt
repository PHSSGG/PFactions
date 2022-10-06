package phss.factions.bukkit.commands.resolvers.admin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.replaceFactionInfo

class AdminUnclaimCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IS_ADMIN)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player

        if (!claimManager.isChunkClaimed(player.location.chunk)) {
            player.sendMessage(messages.getMessage("admin.chunkNotClaimed"))
            return
        }

        val chunkOwner = claimManager.getFactionByChunk(player.location.chunk)!!
        claimManager.unClaimChunk(chunkOwner, player.location.chunk)
        player.sendMessage(messages.getMessage("admin.chunkForceUnClaimed").replaceFactionInfo(chunkOwner, plugin))
    }

}