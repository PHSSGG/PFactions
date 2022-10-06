package phss.factions.request

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import phss.factions.data.domain.Faction
import phss.factions.request.callback.RequestCallback
import phss.factions.request.model.RequestModel
import phss.factions.utils.concurrent.ExpirableHashMap
import phss.factions.utils.concurrent.ExpirableHashMapCallback
import java.util.concurrent.TimeUnit

class RequestService {

    private val requests = ExpirableHashMap(TimeUnit.MINUTES.toMillis(1), object : ExpirableHashMapCallback<RequestModel, RequestCallback> {
        override fun onAdd(key: RequestModel, value: RequestCallback) {}
        override fun onRemove(key: RequestModel, value: RequestCallback?) {
            if (value == null) return

            if (key.accepted) value.onAccepted(Bukkit.getPlayer(key.receiver.second))
            else value.onDenied()
        }
    })

    fun getRequest(receiver: Player, sender: String): RequestModel? {
        return requests.keys.find { it.receiver.second == receiver.uniqueId && it.sender.first == sender }
    }
    fun getRequest(receiver: Faction, sender: String): RequestModel? {
        return requests.keys.find { it.receiver.first == receiver.name && it.sender.first == sender }
    }

    fun createRequest(sender: Player, receiver: Player, callback: RequestCallback): Boolean {
        if (requests.keys.find { it.sender.second == sender.uniqueId && it.receiver.second == receiver.uniqueId } != null) return false

        requests[RequestModel(sender.name to sender.uniqueId, receiver.name to receiver.uniqueId)] = callback
        return true
    }
    fun createRequest(sender: Faction, receiver: Faction, callback: RequestCallback): Boolean {
        if (requests.keys.find { it.sender.first == sender.name && it.receiver.first == receiver.name } != null) return false

        requests[RequestModel(sender.name to sender.members.first(), receiver.name to receiver.members.first())] = callback
        return true
    }

    fun removeRequest(requestModel: RequestModel) {
        requests.removeFromMap(requestModel)
    }

}