package phss.factions.bukkit.commands

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.faction.extensions.isPlayerBanned

abstract class CommandArgument(
    val plugin: PFactions
) {

    val messages = plugin.messages
    val factionManager = plugin.factionManager
    val userManager = plugin.userManager
    val claimManager = plugin.claimManager
    val factionController = plugin.factionController
    val requestService = plugin.requestService

    fun execute(sender: CommandSender, argument: String, args: Array<out String>): Boolean = with(plugin) {
        for (requirement in getRequirements()) {
            if (args.size < requirement.argsSize) {
                sender.sendMessage(messages.getMessage("help.$argument"))
                return false
            }

            when (requirement) {
                CommandRequirement.INTEGER -> try {
                    Integer.valueOf(args[1])
                } catch(ignored: NumberFormatException) {
                    sender.sendMessage(messages.getMessage("onlyNumbers"))
                }
                CommandRequirement.ANY -> if (args.size < 2) {
                    sender.sendMessage(messages.getMessage("help.$argument"))
                    return false
                }
                CommandRequirement.IS_ADMIN -> if (!sender.hasPermission("faction.admin")) {
                    sender.sendMessage(messages.getMessage("notHasPermission"))
                    return false
                }
                CommandRequirement.IS_PLAYER -> if (sender !is Player) {
                    sender.sendMessage(messages.getMessage("onlyInGame"))
                    return false
                }
                CommandRequirement.NEW_FACTION -> if (factionManager.getFactionByName(args[1]) != null) {
                    sender.sendMessage(messages.getMessage("factionAlreadyExists"))
                    return false
                }
                CommandRequirement.ANOTHER_FACTION -> if (factionManager.getFactionByName(args[1]) == null) {
                    sender.sendMessage(messages.getMessage("anotherFactionNotFound"))
                    return false
                }
                CommandRequirement.NOT_IN_FACTION -> if (factionManager.getFactionByPlayer((sender as Player).uniqueId) != null) {
                    sender.sendMessage(messages.getMessage("alreadyInAFaction"))
                    return false
                }
                CommandRequirement.IN_FACTION -> if (factionManager.getFactionByPlayer((sender as Player).uniqueId) == null) {
                    sender.sendMessage(messages.getMessage("notInAFaction"))
                    return false
                }
                CommandRequirement.ANOTHER_ONLINE_PLAYER -> if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(messages.getMessage("targetOnlineNotFound"))
                    return false
                }
                CommandRequirement.ANOTHER_PLAYER -> if (userManager.getUserAccountByName(args[1]) == null) {
                    sender.sendMessage(messages.getMessage("targetNotFound"))
                    return false
                }
                CommandRequirement.ANOTHER_FACTION_AND_PLAYER -> {
                    if (factionManager.getFactionByName(args[1]) == null) {
                        sender.sendMessage(messages.getMessage("anotherFactionNotFound"))
                        return false
                    }
                    if (userManager.getUserAccountByName(args[2]) == null) {
                        sender.sendMessage(messages.getMessage("targetNotFound"))
                        return false
                    }
                }
                CommandRequirement.ANOTHER_IN_ANY_FACTION -> {
                    if (userManager.getUserAccountByName(args[1])?.factionPlayer?.factionId == null) {
                        sender.sendMessage(messages.getMessage("targetNotInAFaction"))
                        return false
                    }
                }
                CommandRequirement.ANOTHER_NOT_IN_FACTION -> {
                    val targetFaction = userManager.getUserAccountByName(args[1])?.factionPlayer?.factionId

                    if (targetFaction != null) {
                        if (factionManager.getFactionByPlayer((sender as Player).uniqueId)?.id == targetFaction) sender.sendMessage(messages.getMessage("targetAlreadyInYourFaction"))
                        else sender.sendMessage(messages.getMessage("targetAlreadyInAFaction"))

                        return false
                    }
                }
                CommandRequirement.ANOTHER_NOT_BANNED -> {
                    val target = userManager.getUserAccountByName(args[1]) ?: return false
                    if (factionManager.getFactionByPlayer((sender as Player).uniqueId)!!.isPlayerBanned(target.uuid)) {
                        sender.sendMessage(messages.getMessage("targetBanned"))
                        return false
                    }
                }
                CommandRequirement.ANOTHER_BANNED -> {
                    val target = userManager.getUserAccountByName(args[1]) ?: return false
                    if (!factionManager.getFactionByPlayer((sender as Player).uniqueId)!!.isPlayerBanned(target.uuid)) {
                        sender.sendMessage(messages.getMessage("targetNotBanned"))
                        return false
                    }
                }
                CommandRequirement.ANOTHER_IN_SAME_FACTION -> {
                    val targetFaction = userManager.getUserAccountByName(args[1])?.factionPlayer?.factionId

                    if (targetFaction == null || factionManager.getFactionByPlayer((sender as Player).uniqueId)?.id != targetFaction) {
                        sender.sendMessage(messages.getMessage("targetNotInSameFaction"))
                        return false
                    }
                }
                CommandRequirement.PERMISSION_LEVEL_INVITE -> {
                    val factionPlayer = userManager.getUserAccountByName(sender.name)?.factionPlayer ?: return false
                    if (factionPlayer.role.permissionLevel < 1) {
                        sender.sendMessage(messages.getMessage("notHasPermissionLevelInvite"))
                        return false
                    }
                }
                CommandRequirement.PERMISSION_LEVEL_CONTROL -> {
                    val factionPlayer = userManager.getUserAccountByName(sender.name)?.factionPlayer ?: return false
                    if (factionPlayer.role.permissionLevel < 2) {
                        sender.sendMessage(messages.getMessage("notHasPermissionLevelControl"))
                        return false
                    }
                }
                CommandRequirement.PERMISSION_LEVEL_LEADER -> {
                    val factionPlayer = userManager.getUserAccountByName(sender.name)?.factionPlayer ?: return false
                    if (factionPlayer.role.permissionLevel < 3) {
                        sender.sendMessage(messages.getMessage("notHasPermissionLevelLeader"))
                        return false
                    }
                }
            }
        }

        invoke(sender, args)
        return true
    }

    abstract fun getRequirements(): List<CommandRequirement>
    abstract fun invoke(sender: CommandSender, args: Array<out String>)

}