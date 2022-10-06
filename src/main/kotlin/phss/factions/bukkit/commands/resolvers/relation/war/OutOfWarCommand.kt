package phss.factions.bukkit.commands.resolvers.relation.war

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

class OutOfWarCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION,
            CommandRequirement.PERMISSION_LEVEL_CONTROL, CommandRequirement.ANOTHER_FACTION
        )
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        if (args[1].lowercase() == "accept") {
            OutOfWarAcceptCommand(plugin).execute(sender, "outofwar_accept", args)
            return
        }
        if (args[1].lowercase() == "deny") {
            OutOfWarDenyCommand(plugin).execute(sender, "outofwar_deny", args)
            return
        }

        val playerFaction = factionManager.getFactionByPlayer((sender as Player).uniqueId) ?: return
        val targetFaction = factionManager.getFactionByName(args[1]) ?: return
        val currentRelation = playerFaction.relations.find { it.factionId == targetFaction.id }

        if (currentRelation != null && currentRelation.type == RelationType.OUT_OF_WAR) {
            sender.sendMessage(messages.getMessage("alreadyOutOfWar"))
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

                    // Out of war relation model for player faction
                    playerFaction.relations.add(FactionRelation(sender.uniqueId, targetFaction.id, RelationType.OUT_OF_WAR))
                    // Out of war relation model for target faction
                    targetFaction.relations.add(FactionRelation(acceptedBy.uniqueId, playerFaction.id, RelationType.OUT_OF_WAR))

                    sender.sendMessage(messages.getMessage("outofwarInviteAccepted").replaceFactionInfo(targetFaction, plugin).replace("{player}", acceptedBy.name))
                    acceptedBy.sendMessage(messages.getMessage("outofwarInviteAccept").replaceFactionInfo(playerFaction, plugin))

                    factionManager.saveFaction(playerFaction, targetFaction)
                }

                override fun onDenied() {
                    sender.sendMessage(messages.getMessage("outofwarInviteDenied").replaceFactionInfo(targetFaction, plugin))
                }
            })

            allowedMembers.forEach {
                Bukkit.getPlayer(it)?.sendMessage(messages.getMessageList("outofwarInviteAlert").replaceFactionInfo(playerFaction, plugin).replace("{author}", sender.name))
            }

            sender.sendMessage(messages.getMessage("outofwarInviteSent").replaceFactionInfo(targetFaction, plugin))
        }
    }

}