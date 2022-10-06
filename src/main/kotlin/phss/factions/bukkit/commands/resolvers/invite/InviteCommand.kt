package phss.factions.bukkit.commands.resolvers.invite

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.request.callback.RequestCallback
import phss.factions.utils.extensions.replace
import phss.factions.utils.extensions.sendMessage

class InviteCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_INVITE,
            CommandRequirement.ANOTHER_ONLINE_PLAYER, CommandRequirement.ANOTHER_NOT_IN_FACTION, CommandRequirement.ANOTHER_NOT_BANNED)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val target = Bukkit.getPlayer(args[1]) ?: return
        val player = sender as Player
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return
        val user = userManager.getUserAccountByUUID(target.uniqueId) ?: userManager.createUser(target)

        if (faction.members.size >= faction.memberCap) {
            player.sendMessage(messages.getMessage("memberLimit").replaceFactionInfo(faction, plugin))
            return
        }
        if (user.tempJoinBan != null) {
            if (System.currentTimeMillis() >= user.tempJoinBan!!) user.tempJoinBan = null
            else {
                player.sendMessage(messages.getMessage("targetTempBanned"))
                return
            }
        }

        requestService.createRequest(sender, target, object : RequestCallback {
            override fun onAccepted(acceptedBy: Player?) {
                factionController.addPlayerToFaction(user, faction)

                factionManager.saveFaction(faction)
                userManager.saveUser(user)

                sender.sendMessage(messages.getMessage("factionInviteAccepted").replace("{player}", target.name))
                target.sendMessage(messages.getMessage("factionInviteAccept").replaceFactionInfo(faction, plugin))
            }

            override fun onDenied() {
                sender.sendMessage(messages.getMessage("factionInviteDenied").replace("{player}", target.name))
                target.sendMessage(messages.getMessage("factionInviteDeny").replaceFactionInfo(faction, plugin))
            }
        })

        sender.sendMessage(messages.getMessage("factionInviteSent").replace("{player}", target.name))
        target.sendMessage(messages.getMessageList("factionInviteAlert").replaceFactionInfo(faction, plugin).replace("{author}", sender.name))
    }

}