package phss.factions.faction.special.entity

import net.citizensnpcs.api.CitizensAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import phss.factions.PFactions
import phss.factions.config.providers.BossConfig
import phss.factions.data.domain.Faction
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.faction.special.BaseEntity
import phss.factions.faction.special.entity.traits.BossTrait
import phss.factions.utils.extensions.replaceColor

class BossEntity(
    private val plugin: PFactions,
    private val faction: Faction,
    private val configuration: BossConfig.Configuration
) : BaseEntity {

    val boss = CitizensAPI.getNPCRegistry().createNPC(EntityType.valueOf(configuration.type), plugin.settings.get.getString("Config.boss.name")?.replace("{faction}", faction.name)?.replaceColor() ?: "${faction.name}'s boss")
    var throwDelay = configuration.throwDelay
    var bossBar: BossBar? = null

    override fun spawn(location: Location) {
        boss.addTrait(BossTrait(plugin, faction, configuration, this))
        boss.spawn(location)

        plugin.specialManager.spawnedBosses[faction.id] = this

        boss.setUseMinecraftAI(false)
        boss.isProtected = false
        boss.navigator.localParameters.attackRange(4.0).attackDelayTicks(10)

        val entity = boss.entity as LivingEntity
        entity.setMetadata("faction_boss", FixedMetadataValue(plugin, "${faction.id}"))

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = configuration.health
        entity.health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = configuration.damage

        bossBar = Bukkit.createBossBar(plugin.settings.get.getString("Config.boss.bossBar.title")!!.replaceFactionInfo(faction, plugin).replaceColor(), BarColor.RED, BarStyle.SOLID)
        bossBar!!.run {
            isVisible = true
            progress = entity.health / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue

            val distance = plugin.settings.get.getDouble("Config.boss.bossBar.showDistance")
            entity.getNearbyEntities(distance, distance, distance).filterIsInstance<Player>().forEach(this::addPlayer)
        }
    }

    override fun despawn() {
        bossBar?.run {
            isVisible = false
            removeAll()

            bossBar = null
        }

        (boss.entity as? LivingEntity?)?.health = 0.0
        boss.despawn()
        boss.destroy()

        plugin.specialManager.spawnedBosses.remove(faction.id)
    }

    override fun bukkitLocation(): Location {
        return boss.entity.location
    }

}