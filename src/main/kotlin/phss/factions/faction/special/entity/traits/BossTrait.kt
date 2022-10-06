package phss.factions.faction.special.entity.traits

import net.citizensnpcs.api.event.NPCDeathEvent
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import net.citizensnpcs.api.trait.TraitName
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.Fireball
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.metadata.FixedMetadataValue
import phss.factions.PFactions
import phss.factions.bukkit.view.openFactionBossControlMenu
import phss.factions.config.providers.BossConfig
import phss.factions.data.domain.Faction
import phss.factions.faction.extensions.hasSafeRelation
import phss.factions.faction.special.entity.BossEntity

@TraitName("bosstrait")
class BossTrait(
    private val plugin: PFactions,
    val faction: Faction,
    private val configuration: BossConfig.Configuration,
    val bossEntity: BossEntity
) : Trait("bosstrait") {

    var forceAttack: Player? = null
    val friend = ArrayList<String>()

    override fun run() {
        if (npc.entity == null) return
        if ((npc.entity as LivingEntity).isDead) {
            bossEntity.despawn()
            return
        }

        bossEntity.bossBar?.run {
            val entity = npc.entity as LivingEntity
            if (!entity.isDead) {
                val distance = plugin.settings.get.getDouble("Config.boss.bossBar.showDistance")
                entity.getNearbyEntities(distance, distance, distance).filterIsInstance<Player>().filter(players::contains).forEach(this::addPlayer)
                progress = entity.health / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            }
        }

        if (forceAttack == null) {
            val distance = plugin.settings.get.getDouble("Config.boss.attackDistance")
            val nearestPlayer = npc.entity.getNearbyEntities(distance, distance, distance).filterIsInstance(Player::class.java).firstOrNull { canAttack(it) }
            if (nearestPlayer == null) {
                val leader = Bukkit.getPlayer(faction.leader)

                if (leader == null) bossEntity.despawn()
                else {
                    if (npc.entity.location.distance(leader.location) > 3 && (npc.navigator.entityTarget == null || npc.navigator.entityTarget.target.isDead)) npc.navigator.setTarget(
                        leader.location
                    )
                }

                return
            }

            if ((npc.navigator.entityTarget == null || npc.navigator.entityTarget.target.isDead) || npc.navigator.entityTarget.target.uniqueId != nearestPlayer.uniqueId) npc.navigator.setTarget(nearestPlayer, true)
        } else if (forceAttack!!.isOnline) {
            if ((npc.navigator.entityTarget == null || npc.navigator.entityTarget.target.isDead) || npc.navigator.entityTarget.target.uniqueId != forceAttack!!.uniqueId) npc.navigator.setTarget(forceAttack, true)
        }

        if (configuration.canThrow) {
            bossEntity.throwDelay -= 1
            if (bossEntity.throwDelay <= 0) {
                (npc.entity as LivingEntity).launchProjectile(Fireball::class.java).setMetadata("boss_fireball", FixedMetadataValue(plugin, "${faction.id}"))
                bossEntity.throwDelay = configuration.throwDelay
            }
        }
    }

    @EventHandler
    fun onInteract(event: NPCRightClickEvent) {
        if (event.npc.id == npc.id && event.clicker.uniqueId == faction.leader) {
            event.clicker.openFactionBossControlMenu(plugin, this)
        }
    }

    @EventHandler
    fun onDeath(event: NPCDeathEvent) {
        if (event.npc.id == npc.id) bossEntity.despawn()
    }

    @EventHandler
    fun onFireballDamage(event: ProjectileHitEvent) {
        if (event.entity.hasMetadata("boss_fireball") && event.hitEntity != null) {
            (event.hitEntity as LivingEntity).damage(configuration.throwDamage, event.entity)

            event.isCancelled = true
            event.entity.remove()
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.damager.hasMetadata("faction_boss") && configuration.hasCritical) {
            val random = Math.random() * 100.0
            if (random > configuration.criticalChance) return

            event.damage = configuration.criticalDamage
        }
    }

    private fun canAttack(entity: Entity): Boolean {
        val account = plugin.userManager.getUserAccountByUUID(entity.uniqueId) ?: return true
        if (account.factionPlayer == null) return true

        val targetFaction = plugin.factionManager.getFactionById(account.factionPlayer!!.factionId) ?: return true
        return targetFaction.id != faction.id && !faction.hasSafeRelation(targetFaction) && entity.name !in friend
    }

}