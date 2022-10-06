package phss.factions.bukkit.commands.resolvers.admin

import org.bukkit.command.CommandSender
import phss.factions.PFactions
import phss.factions.bukkit.commands.CommandArgument
import phss.factions.bukkit.commands.CommandRequirement

class ReloadCommand(
    plugin: PFactions
) : CommandArgument(plugin) {

    override fun getRequirements(): List<CommandRequirement> {
        return listOf(CommandRequirement.IS_ADMIN)
    }

    override fun invoke(sender: CommandSender, args: Array<out String>) {
        plugin.loadFiles()
        plugin.specialManager.loadActions()

        sender.sendMessage("admin.reload")
    }

}