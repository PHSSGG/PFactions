package phss.factions.utils.extensions

import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

fun Plugin.registerEvents(
    vararg listeners: Listener
) = listeners.forEach { server.pluginManager.registerEvents(it, this) }

interface WithPlugin<T : Plugin> { val plugin: T }