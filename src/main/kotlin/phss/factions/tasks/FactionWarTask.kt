package phss.factions.tasks

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import phss.factions.PFactions
import phss.factions.faction.extensions.replaceFactionInfo

class FactionWarTask(
    private val plugin: PFactions
) : BukkitRunnable() {

    fun start() {
        runTaskTimerAsynchronously(plugin, 0L, 20*60*10L)
    }

    override fun run() {
        val factions = plugin.factionManager.getFactions().filter { it.wars.isNotEmpty() }
        for (faction in factions) {
            val wars = ArrayList(faction.wars.filter { System.currentTimeMillis() >= it.duration })
            wars.forEach {
                faction.members.forEach { member ->
                    val targetFaction = plugin.factionManager.getFactionById(it.factionId)!!
                    Bukkit.getPlayer(member)?.sendMessage(plugin.messages.getMessage("warExpired").replaceFactionInfo(targetFaction, plugin))
                }
                faction.wars.remove(it)
            }

            if (wars.isNotEmpty()) plugin.factionManager.saveFaction(faction)
        }
    }

}