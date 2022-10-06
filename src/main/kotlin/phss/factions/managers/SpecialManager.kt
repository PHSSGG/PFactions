package phss.factions.managers

import org.bukkit.NamespacedKey
import phss.factions.PFactions
import phss.factions.faction.special.SpecialAction
import phss.factions.faction.special.actions.SpiderSpecialAction
import phss.factions.faction.special.actions.ZombieSpecialAction
import phss.factions.faction.special.entity.BossEntity

class SpecialManager(
    private val plugin: PFactions
) {

    val bossNamespacedKey = NamespacedKey(plugin, "fboss")

    val spawnedBosses = HashMap<Int, BossEntity>()
    val actions = HashMap<String, SpecialAction>()

    fun loadActions() {
        val settings = plugin.settings.get

        for (selected in settings.getConfigurationSection("Special")!!.getKeys(false))
            registerAction(selected)
    }

    fun registerAction(key: String) {
        registerAction(when (key.lowercase()) {
            "zombie" -> ZombieSpecialAction(plugin)
            else -> SpiderSpecialAction(plugin)
        })
    }

    fun registerAction(action: SpecialAction) {
        actions[action.key] = action
        plugin.server.pluginManager.registerEvents(action, plugin)
    }

}