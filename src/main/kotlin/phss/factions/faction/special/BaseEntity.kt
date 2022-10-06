package phss.factions.faction.special

import org.bukkit.Location

interface BaseEntity {

    fun spawn(location: Location)
    fun despawn()

    fun bukkitLocation(): Location

}