package phss.factions.bukkit.commands

enum class CommandRequirement(
    val argsSize: Int = 0
) {

    // Argument requirements
    NEW_FACTION(2), ANOTHER_FACTION(2), ANOTHER_PLAYER(2), ANOTHER_ONLINE_PLAYER(2), INTEGER(2), ANY(2), ANOTHER_FACTION_AND_PLAYER(3),

    // Self requirements
    NOT_IN_FACTION, IN_FACTION, IS_PLAYER, IS_ADMIN,

    // Permissions
    PERMISSION_LEVEL_INVITE, PERMISSION_LEVEL_CONTROL, PERMISSION_LEVEL_LEADER,

    // Another requirements
    ANOTHER_NOT_IN_FACTION, ANOTHER_IN_SAME_FACTION, ANOTHER_IN_ANY_FACTION, ANOTHER_NOT_BANNED, ANOTHER_BANNED

}