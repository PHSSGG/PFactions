package phss.factions.utils.concurrent

interface ExpirableHashMapCallback<K, V> {

    fun onAdd(key: K, value: V)
    fun onRemove(key: K, value: V?)

}