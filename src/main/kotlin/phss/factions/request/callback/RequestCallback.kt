package phss.factions.request.callback

import org.bukkit.entity.Player

interface RequestCallback {

    fun onAccepted(acceptedBy: Player?)
    fun onDenied()

}