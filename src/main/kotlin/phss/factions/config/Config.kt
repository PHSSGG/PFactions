package phss.factions.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.file.Files

class Config(
    private val plugin: JavaPlugin,
    private val fileName: String
) {

    private lateinit var file: File
    private lateinit var fileConfiguration: FileConfiguration

    val get: FileConfiguration
        get() = fileConfiguration

    fun load() {
        File(plugin.dataFolder.path).run {
            if (!exists()) mkdirs()
        }
        file = File(plugin.dataFolder, "$fileName.yml")
        if (!file.exists()) {
            val inputStream = plugin.getResource("$fileName.yml")!!
            Files.copy(inputStream, file.toPath())
        }
        fileConfiguration = YamlConfiguration()
        fileConfiguration.load(file)
    }
    fun reload() = load()

    fun save() {
        fileConfiguration.save(file)
    }

}