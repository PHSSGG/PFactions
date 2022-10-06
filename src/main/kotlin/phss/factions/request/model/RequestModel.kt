package phss.factions.request.model

import java.util.*

class RequestModel(
    val sender: Pair<String, UUID>,
    var receiver: Pair<String, UUID>,
    var accepted: Boolean = false
)