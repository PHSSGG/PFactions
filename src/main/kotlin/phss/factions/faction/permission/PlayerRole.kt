package phss.factions.faction.permission

enum class PlayerRole(val permissionLevel: Int) {

    DEFAULT(0), MEMBER(0), COMMANDER(1), OWNER(2), EXILE(3);

    fun isMax(): Boolean {
        return this == OWNER
    }

    fun isMin(): Boolean {
        return this == DEFAULT
    }

    fun getNextRole(): PlayerRole {
        return when (this) {
            DEFAULT -> MEMBER
            MEMBER -> COMMANDER
            else -> OWNER
        }
    }

    fun getPreviousRole(): PlayerRole {
        return when (this) {
            EXILE -> OWNER
            OWNER -> COMMANDER
            COMMANDER -> MEMBER
            else -> DEFAULT
        }
    }

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }

}