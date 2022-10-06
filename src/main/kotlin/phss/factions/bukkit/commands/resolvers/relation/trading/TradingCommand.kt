package phss.factions.bukkit.commands.resolvers.relation.trading

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.FactionRelation
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.faction.types.RelationType
import phss.factions.request.callback.RequestCallback
import phss.factions.utils.extensions.replace
import phss.factions.utils.extensions.sendMessage

class TradingCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION,
            CommandRequirement.PERMISSION_LEVEL_CONTROL, CommandRequirement.ANY
        )
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        if (args[1].lowercase() == "accept") {
            TradingAcceptCommand(plugin).execute(sender, "trading_accept", args)
            return
        }
        if (args[1].lowercase() == "deny") {
            TradingDenyCommand(plugin).execute(sender, "trading_deny", args)
            return
        }

        val playerFaction = factionManager.getFactionByPlayer((sender as Player).uniqueId) ?: return
        val targetFaction = factionManager.getFactionByName(args[1])
        if (targetFaction == null) {
            sender.sendMessage(messages.getMessage("anotherFactionNotFound"))
            return
        }
        val currentRelation = playerFaction.relations.find { it.factionId == targetFaction.id }

        if (currentRelation != null && currentRelation.type == RelationType.TRADING) {
            sender.sendMessage(messages.getMessage("alreadyTrading"))
            return
        }

        val allowedMembers = targetFaction.members.filter { Bukkit.getPlayer(it) != null && userManager.getUserAccountByUUID(it)?.factionPlayer?.role?.permissionLevel ?: 0 >= 2 }
        if (allowedMembers.isEmpty()) sender.sendMessage(messages.getMessage("anyoneWithPermissionOnlineInTargetFaction"))
        else {
            requestService.createRequest(playerFaction, targetFaction, object : RequestCallback {
                override fun onAccepted(acceptedBy: Player?) {
                    if (acceptedBy == null) return
                    if (currentRelation != null) {
                        playerFaction.relations.remove(currentRelation)
                        targetFaction.relations.remove(targetFaction.relations.find { it.factionId == playerFaction.id })
                    }

                    // Trading relation model for player faction
                    playerFaction.relations.add(FactionRelation(sender.uniqueId, targetFaction.id, RelationType.TRADING))
                    // Trading relation model for target faction
                    targetFaction.relations.add(FactionRelation(acceptedBy.uniqueId, playerFaction.id, RelationType.TRADING))

                    sender.sendMessage(messages.getMessage("tradingInviteAccepted").replaceFactionInfo(targetFaction, plugin).replace("{player}", acceptedBy.name))
                    acceptedBy.sendMessage(messages.getMessage("tradingInviteAccept").replaceFactionInfo(playerFaction, plugin))

                    factionManager.saveFaction(playerFaction, targetFaction)
                }

                override fun onDenied() {
                    sender.sendMessage(messages.getMessage("tradingInviteDenied").replaceFactionInfo(targetFaction, plugin))
                }
            })

            allowedMembers.forEach {
                Bukkit.getPlayer(it)?.sendMessage(messages.getMessageList("tradingInviteAlert").replaceFactionInfo(playerFaction, plugin).replace("{author}", sender.name))
            }

            sender.sendMessage(messages.getMessage("tradingInviteSent").replaceFactionInfo(targetFaction, plugin))
        }
    }

}