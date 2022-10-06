package phss.factions.tasks

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import phss.factions.PFactions
import kotlin.math.min

class PowerIncrementTask(
    private val plugin: PFactions
) : BukkitRunnable() {

    fun start() {
        runTaskTimerAsynchronously(plugin, 0L, 20L*60*plugin.settings.get.getInt("Config.power.incrementTime"))
    }

    override fun run() {
        val users = plugin.userManager.getUsers().filter { Bukkit.getPlayer(it.uuid) != null }
        users.forEach {
            it.power = min(it.power + plugin.settings.get.getDouble("Config.power.incrementByTimeAmount"), it.maxPower)

            if (it.factionPlayer != null)
                plugin.factionController.updateFactionPower(plugin.factionManager.getFactionById(it.factionPlayer!!.factionId)!!)
        }
    }

}