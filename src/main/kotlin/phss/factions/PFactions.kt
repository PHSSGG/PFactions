package phss.factions

import org.bukkit.plugin.java.JavaPlugin
import phss.factions.bukkit.commands.FactionCommand
import phss.factions.bukkit.events.FactionListeners
import phss.factions.bukkit.events.PlayerListeners
import phss.factions.config.Config
import phss.factions.config.providers.MessagesConfig
import phss.factions.conversation.listeners.FactionWarConversation
import phss.factions.data.dao.DataDao
import phss.factions.data.dao.impl.FactionDaoImpl
import phss.factions.data.dao.impl.UserDaoImpl
import phss.factions.data.domain.Faction
import phss.factions.data.domain.User
import phss.factions.data.repository.DataRepository
import phss.factions.data.repository.impl.FactionRepositoryImpl
import phss.factions.data.repository.impl.UserRepositoryImpl
import phss.factions.database.DatabaseManager
import phss.factions.faction.controller.FactionController
import phss.factions.managers.ClaimManager
import phss.factions.managers.FactionManager
import phss.factions.managers.SpecialManager
import phss.factions.managers.UserManager
import phss.factions.request.RequestService
import phss.factions.tasks.FactionWarTask
import phss.factions.tasks.PowerIncrementTask
import phss.factions.bukkit.events.MenuController
import phss.factions.utils.extensions.registerEvents
import java.util.*

class PFactions : JavaPlugin() {

    val factionManager = FactionManager(this)
    val userManager = UserManager(this)
    val claimManager = ClaimManager(this)
    val specialManager = SpecialManager(this)
    val databaseManager = DatabaseManager(this)

    private lateinit var userDao: DataDao<UUID, User>
    private lateinit var factionDao: DataDao<Int, Faction>

    lateinit var userRepository: DataRepository<UUID, User>
    lateinit var factionRepository: DataRepository<Int, Faction>

    val factionController = FactionController(this)
    val factionWarConversation = FactionWarConversation(this)

    val requestService = RequestService()

    // normal files
    val storage = Config(this, "storage")
    val settings = Config(this, "settings")
    val lang = Config(this, "lang")
    // menu files
    val mainMenu = Config(this, "main_menu")
    val factionTopMenu = Config(this, "faction_top_menu")
    val factionMembersMenu = Config(this, "faction_members_menu")
    val factionRelationsMenu = Config(this, "faction_relations_menu")
    val factionInfoMenu = Config(this, "faction_info_menu")
    val factionSpecialMenu = Config(this, "faction_special_menu")

    val messages = MessagesConfig(this)

    override fun onEnable() {
        loadFiles()

        databaseManager.start()
        userDao = UserDaoImpl(databaseManager).apply { load() }
        factionDao = FactionDaoImpl(databaseManager).apply { load() }

        userRepository = UserRepositoryImpl(userDao)
        factionRepository = FactionRepositoryImpl(factionDao)

        specialManager.loadActions()

        FactionWarTask(this).start()
        PowerIncrementTask(this).start()

        getCommand("faction")?.setExecutor(FactionCommand(this))
        registerEvents(MenuController(), FactionListeners(this), PlayerListeners(this))
    }

    override fun onDisable() {
        databaseManager.process.task.queue.forEach { data ->
            if (data.second) databaseManager.controller.saveData(data.first)
            else databaseManager.controller.deleteData(data.first)
        }

        specialManager.spawnedBosses.values.forEach { it.despawn() }
    }

    fun loadFiles() {
        storage.load()
        settings.load()
        lang.load()
        mainMenu.load()
        factionTopMenu.load()
        factionMembersMenu.load()
        factionRelationsMenu.load()
        factionInfoMenu.load()
        factionSpecialMenu.load()
    }

}