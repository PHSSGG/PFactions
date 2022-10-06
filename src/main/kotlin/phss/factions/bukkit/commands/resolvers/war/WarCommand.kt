package phss.factions.bukkit.commands.resolvers.war

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement
import phss.factions.faction.FactionRelation
import phss.factions.faction.FactionWar
import phss.factions.faction.FactionWarProposal
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.faction.types.RelationType
import phss.factions.request.callback.RequestCallback
import phss.factions.utils.extensions.replace
import phss.factions.utils.extensions.sendMessage
import java.util.concurrent.TimeUnit

class WarCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(
            CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION,
            CommandRequirement.PERMISSION_LEVEL_LEADER, CommandRequirement.ANY
        )
    }

    val factionWarConversation = plugin.factionWarConversation

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        if (args[1].lowercase() == "accept") {
            WarAcceptCommand(plugin).execute(sender, "war_accept", args)
            return
        }
        if (args[1].lowercase() == "deny") {
            WarDenyCommand(plugin).execute(sender, "war_deny", args)
            return
        }

        val playerFaction = factionManager.getFactionByPlayer((sender as Player).uniqueId) ?: return
        val targetFaction = factionManager.getFactionByName(args[1])
        if (targetFaction == null) {
            sender.sendMessage(messages.getMessage("anotherFactionNotFound"))
            return
        }
        val currentRelation = playerFaction.relations.find { it.factionId == targetFaction.id }

        if (playerFaction.wars.any { it.factionId == targetFaction.id }) {
            sender.sendMessage(messages.getMessage("alreadyWar"))
            return
        }

        if (factionWarConversation.getProposalByFactionId(playerFaction.id) != null) {
            sender.sendMessage(messages.getMessage("alreadyInAWarInvite"))
            return
        }
        if (factionWarConversation.getProposalByFactionId(targetFaction.id) != null) {
            sender.sendMessage(messages.getMessage("targetAlreadyInAWarInvite"))
            return
        }

        val targetFactionLeader = Bukkit.getPlayer(targetFaction.leader)
        if (targetFactionLeader == null) sender.sendMessage(messages.getMessage("anyoneWithPermissionOnlineInTargetFaction"))
        else {
            requestService.createRequest(playerFaction, targetFaction, object : RequestCallback {
                override fun onAccepted(acceptedBy: Player?) {
                    if (currentRelation != null) {
                        playerFaction.relations.remove(currentRelation)
                        targetFaction.relations.remove(targetFaction.relations.find { it.factionId == playerFaction.id })
                    }

                    sender.sendMessage(messages.getMessageList("warInviteAccepted").replaceFactionInfo(targetFaction, plugin).replace("{player}", targetFactionLeader.name))
                    targetFactionLeader.sendMessage(messages.getMessageList("warInviteAccept").replaceFactionInfo(playerFaction, plugin))

                    val proposal = FactionWarProposal(playerFaction, targetFaction, object : FactionWarProposal.ProposalCallback {
                        override fun onFactionAccept(factionId: Int) {
                            val proposal = factionWarConversation.getProposalByFactionId(factionId)
                            factionWarConversation.proposals.remove(proposal)

                            // Enemy relation model for player faction
                            playerFaction.relations.add(FactionRelation(sender.uniqueId, targetFaction.id, RelationType.ENEMY))
                            // Enemy relation model for target faction
                            targetFaction.relations.add(FactionRelation(targetFactionLeader.uniqueId, playerFaction.id, RelationType.ENEMY))

                            sender.sendMessage(messages.getMessageList("warStarted").replace("{amount}", proposal!!.currentDuration.toString()).replaceFactionInfo(targetFaction, plugin).replace("{player}", targetFactionLeader.name))
                            targetFactionLeader.sendMessage(messages.getMessageList("warStart").replace("{amount}", proposal.currentDuration.toString()).replaceFactionInfo(playerFaction, plugin))

                            val duration = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(proposal.currentDuration.toLong())
                            // War model for player faction
                            playerFaction.wars.add(FactionWar(targetFaction.id, sender.uniqueId, targetFactionLeader.uniqueId, duration))
                            // War model for target faction
                            targetFaction.wars.add(FactionWar(playerFaction.id, sender.uniqueId, targetFactionLeader.uniqueId, duration))

                            factionManager.saveFaction(playerFaction, targetFaction)
                        }
                        override fun onFactionDeny(factionId: Int) {
                            val proposal = factionWarConversation.getProposalByFactionId(factionId)
                            factionWarConversation.proposals.remove(proposal)

                            sender.sendMessage(messages.getMessage("warInviteCanceled").replaceFactionInfo(targetFaction, plugin).replace("{player}", targetFactionLeader.name))
                            targetFactionLeader.sendMessage(messages.getMessage("warInviteCancel").replaceFactionInfo(playerFaction, plugin))
                        }

                        override fun onTimeProposed(newTime: Int) {
                            sender.sendMessage(messages.getMessageList("warInviteTimeProposed").replace("{amount}", newTime.toString()).replaceFactionInfo(targetFaction, plugin).replace("{player}", targetFactionLeader.name))
                            targetFactionLeader.sendMessage(messages.getMessageList("warInviteTimeProposal").replace("{amount}", newTime.toString()).replaceFactionInfo(playerFaction, plugin))
                        }
                    })

                    factionWarConversation.proposals.add(proposal)
                }

                override fun onDenied() {
                    sender.sendMessage(messages.getMessage("warInviteDenied").replaceFactionInfo(targetFaction, plugin))
                }
            })

            targetFactionLeader.sendMessage(messages.getMessageList("warInviteAlert").replaceFactionInfo(playerFaction, plugin).replace("{author}", sender.name))
            sender.sendMessage(messages.getMessage("warInviteSent").replaceFactionInfo(targetFaction, plugin))
        }
    }

}