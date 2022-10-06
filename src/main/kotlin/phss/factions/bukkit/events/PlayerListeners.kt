package phss.factions.bukkit.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerEggThrowEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import phss.factions.PFactions
import phss.factions.config.providers.BossConfig
import phss.factions.faction.special.entity.BossEntity

class PlayerListeners(
    private val plugin: PFactions
) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (plugin.userManager.getUserAccountByUUID(event.player.uniqueId) == null)
            plugin.userManager.createUser(event.player, true)

        plugin.userManager.getUserAccountByUUID(event.player.uniqueId)?.name = event.player.name
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        plugin.userManager.getUserAccountByUUID(event.player.uniqueId)?.lastSeen = System.currentTimeMillis()
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val settings = plugin.settings.get

        if (event.entity.killer != null) {
            val killerAccount = plugin.userManager.getUserAccountByUUID(event.entity.killer!!.uniqueId)
            if (killerAccount != null) {
                killerAccount.kills += 1
                killerAccount.power += settings.getDouble("Config.power.addPowerPerKill")

                if (killerAccount.factionPlayer != null) {
                    val faction = plugin.factionManager.getFactionById(killerAccount.factionPlayer!!.factionId)
                    if (faction != null) {
                        plugin.factionController.updateFactionPower(faction)
                        plugin.factionManager.saveFaction(faction)
                    }
                }

                plugin.userManager.saveUser(killerAccount)
            }
        }

        val userAccount = plugin.userManager.getUserAccountByUUID(event.entity.uniqueId) ?: return
        userAccount.power -= settings.getDouble("Config.power.removePowerPerDeath")
        if (userAccount.power < settings.getDouble("Config.power.minPower")) userAccount.power = settings.getDouble("Config.power.minPower")

        if (userAccount.factionPlayer != null) {
            val faction = plugin.factionManager.getFactionById(userAccount.factionPlayer!!.factionId)!!

            plugin.factionController.updateFactionPower(faction)
            plugin.factionManager.saveFaction(faction)
        }

        plugin.userManager.saveUser(userAccount)
    }

    @EventHandler
    fun onEggThrow(event: PlayerEggThrowEvent) {
        if (event.egg.item.hasItemMeta() && event.egg.item.itemMeta!!.persistentDataContainer.has(plugin.specialManager.bossNamespacedKey, PersistentDataType.INTEGER)) {
            val faction = plugin.factionManager.getFactionById(event.egg.item.itemMeta!!.persistentDataContainer.get(plugin.specialManager.bossNamespacedKey, PersistentDataType.INTEGER)!!) ?: return
            val entity = BossEntity(plugin, faction, BossConfig(plugin.settings.get, faction.type!!).configuration)

            entity.spawn(event.egg.location)
            event.egg.remove()
            event.isHatching = false

            plugin.specialManager.spawnedBosses[faction.id] = entity
        }
    }

}