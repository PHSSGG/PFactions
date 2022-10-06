package phss.factions.bukkit.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.resolvers.*
import phss.factions.bukkit.commands.resolvers.admin.*
import phss.factions.bukkit.commands.resolvers.chat.*
import phss.factions.bukkit.commands.resolvers.claim.*
import phss.factions.bukkit.commands.resolvers.home.*
import phss.factions.bukkit.commands.resolvers.invite.*
import phss.factions.bukkit.commands.resolvers.relation.RelationCommand
import phss.factions.bukkit.commands.resolvers.relation.ally.AllyCommand
import phss.factions.bukkit.commands.resolvers.relation.enemy.EnemyCommand
import phss.factions.bukkit.commands.resolvers.relation.hostile.HostileCommand
import phss.factions.bukkit.commands.resolvers.relation.trading.TradingCommand
import phss.factions.bukkit.commands.resolvers.relation.war.OutOfWarCommand
import phss.factions.bukkit.commands.resolvers.war.WarCommand
import phss.factions.bukkit.view.openWithFactionMenu
import phss.factions.bukkit.view.openWithoutFactionMenu
import phss.factions.utils.extensions.sendMessage

class FactionCommand(
    private val plugin: PFactions
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (sender !is Player) sender.sendMessage(plugin.messages.getMessageList("help.default"))
            else {
                if (plugin.userManager.getUserAccountByUUID(sender.uniqueId)?.factionPlayer == null) sender.openWithoutFactionMenu(plugin)
                else sender.openWithFactionMenu(plugin)
            }
            return true
        }

        if (sender is Player)
            plugin.userManager.getUserAccountByUUID(sender.uniqueId)?.lastSeen = System.currentTimeMillis()

        when (args[0].lowercase()) {
            "create" -> CreateCommand(plugin).execute(sender, "create", args)
            "disband" -> DisbandCommand(plugin).execute(sender, "disband", args)
            "leave" -> LeaveCommand(plugin).execute(sender, "leave", args)
            "invite" -> InviteCommand(plugin).execute(sender, "invite", args)
            "accept" -> InviteAcceptCommand(plugin).execute(sender, "accept", args)
            "deny" -> InviteDenyCommand(plugin).execute(sender, "deny", args)
            "promote" -> PromoteCommand(plugin).execute(sender, "promote", args)
            "demote" -> DemoteCommand(plugin).execute(sender, "demote", args)
            "ban" -> BanCommand(plugin).execute(sender, "ban", args)
            "unban" -> UnbanCommand(plugin).execute(sender, "unban", args)
            "kick" -> KickCommand(plugin).execute(sender, "kick", args)
            "config", "set" -> ConfigCommand(plugin).execute(sender, "config", args)
            "list" -> ListCommand(plugin).execute(sender, "list", args)
            "top" -> TopCommand(plugin).execute(sender, "top", args)
            "relation", "relations" -> RelationCommand(plugin).execute(sender, "relation", args)
            "ally" -> AllyCommand(plugin).execute(sender, "ally", args)
            "enemy" -> EnemyCommand(plugin).execute(sender, "enemy", args)
            "hostile" -> HostileCommand(plugin).execute(sender, "hostile", args)
            "trading" -> TradingCommand(plugin).execute(sender, "trading", args)
            "outofwar" -> OutOfWarCommand(plugin).execute(sender, "outofwar", args)
            "show" -> ShowCommand(plugin).execute(sender, "show", args)
            "home" -> HomeCommand(plugin).execute(sender, "home", args)
            "sethome" -> SetHomeCommand(plugin).execute(sender, "sethome", args)
            "claim" -> ClaimCommand(plugin).execute(sender, "claim", args)
            "unclaim" -> UnclaimCommand(plugin).execute(sender, "claim", args)
            "war" -> WarCommand(plugin).execute(sender, "war", args)
            "chat", "c" -> ChatCommand(plugin).execute(sender, "chat", args)
            "allychat", "ac" -> AllyChatCommand(plugin).execute(sender, "allychat", args)
            "tradingchat", "tc" -> TradingChatCommand(plugin).execute(sender, "tradingchat", args)
            "outofwarchat", "ofwc" -> OutOfWarChatCommand(plugin).execute(sender, "outofwarchat", args)
            "adminclaim" -> AdminClaimCommand(plugin).execute(sender, "adminclaim", args)
            "adminunclaim" -> AdminUnclaimCommand(plugin).execute(sender, "adminunclaim", args)
            "admindisband" -> AdminDisbandCommand(plugin).execute(sender, "admindisband", args)
            "reload" -> ReloadCommand(plugin).execute(sender, "reload", args)
            "setleader" -> SetLeaderCommand(plugin).execute(sender, "setleader", args)
            "setplayer" -> SetPlayerCommand(plugin).execute(sender, "setplayer", args)
            "setrole" -> SetRoleCommand(plugin).execute(sender, "setrole", args)
            "giveboss" -> GiveBossCommand(plugin).execute(sender, "giveboss", args)
            else -> sender.sendMessage(plugin.messages.getMessageList("help.default"))
        }
        return true
    }

}