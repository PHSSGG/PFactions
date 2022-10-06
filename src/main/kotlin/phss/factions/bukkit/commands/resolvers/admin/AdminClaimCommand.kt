package phss.factions.bukkit.commands.resolvers.admin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.replaceFactionInfo

class AdminClaimCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IS_ADMIN, CommandRequirement.ANOTHER_FACTION)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val targetFaction = factionManager.getFactionByName(args[1])!!
        val userAccount = userManager.getUserAccountByUUID(player.uniqueId) ?: userManager.createUser(player, false)

        if (claimManager.isChunkClaimed(player.location.chunk)) {
            val chunkOwner = claimManager.getFactionByChunk(player.location.chunk)!!
            claimManager.unClaimChunk(chunkOwner, player.location.chunk)
        }

        claimManager.claimChunk(targetFaction, userAccount, player.location.chunk, true)
        player.sendMessage(messages.getMessage("admin.chunkForceClaimed").replaceFactionInfo(targetFaction, plugin))
    }

}