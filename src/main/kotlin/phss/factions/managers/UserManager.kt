package phss.factions.managers

import org.bukkit.entity.Player
import phss.factions.PFactions
import phss.factions.data.domain.User
import java.util.*

class UserManager(
    private val plugin: PFactions
) {

    fun getUsers(): List<User> {
        return plugin.userRepository.data
    }

    fun getUserAccountByUUID(uuid: UUID): User? {
        return plugin.userRepository[uuid]
    }

    fun getUserAccountByName(name: String): User? {
        return plugin.userRepository.data.find { it.name == name }
    }

    fun createUser(player: Player, save: Boolean = false): User {
        val account = User(player.uniqueId, player.name, maxPower = plugin.settings.get.getDouble("Config.power.maxPlayerPower"))
        if (save) plugin.userRepository.create(account)

        return account
    }

    fun saveUser(user: User) {
        plugin.userRepository.edit(user)
    }

}