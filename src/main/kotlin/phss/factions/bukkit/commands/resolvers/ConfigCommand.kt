package phss.factions.bukkit.commands.resolvers

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement

class ConfigCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_PLAYER, CommandRequirement.IN_FACTION, CommandRequirement.PERMISSION_LEVEL_CONTROL, CommandRequirement.ANY)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val faction = factionManager.getFactionByPlayer(player.uniqueId) ?: return

        val attribute = args[1].uppercase()
        if (attribute == "HOME") {
            player.chat("/f sethome")
            return
        }
        if (args.size < 3) {
            sender.sendMessage(messages.getMessage("help.config"))
            return
        }

        var value = ""
        (2 until args.size).forEach { i ->
            value = "$value${args[i]} "
        }
        when (attribute) {
            "DISPLAY" -> faction.display = value
            "MOTD" -> faction.motd = value
            "CAP" -> faction.memberCap = args[2].toInt()
        }

        factionManager.saveFaction(faction)
        sender.sendMessage(messages.getMessage("configAttributeSet").replace("{attribute}", attribute).replace("{value}", value))
    }

}