package phss.factions.faction.types

import java.io.Serializable

enum class RelationType : Serializable {

    OUT_OF_WAR, TRADING, HOSTILE, ENEMY, ALLY;

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }

}