package phss.factions.utils.concurrent

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ExpirableHashMap<K, V> : ConcurrentHashMap<K, V> {

    private val timeMap = ConcurrentHashMap<K, Long>()
    private var callback: ExpirableHashMapCallback<K, V>
    private var expireInMillis: Long
    private var isAlive = true

    constructor(callback: ExpirableHashMapCallback<K, V>) {
        this.callback = callback
        expireInMillis = 30000
        initialize()
    }

    constructor(expireInMillis: Long, callback: ExpirableHashMapCallback<K, V>) {
        this.expireInMillis = expireInMillis
        this.callback = callback
        initialize()
    }

    private fun initialize() {
        CleanerThread().start()
    }

    override fun put(key: K, value: V): V? {
        check(isAlive) {
            initialize()
        }
        val date = Date()
        timeMap[key] = date.time

        return super.put(key!!, value!!).apply {
            callback.onAdd(key, value)
        }
    }

    override fun putAll(from: Map<out K, V>) {
        check(isAlive) {
            initialize()
        }
        for (key in from.keys) {
            put(key, from[key]!!)
        }
    }

    override fun putIfAbsent(key: K, value: V): V? {
        check(isAlive) {
            initialize()
        }
        return if (!containsKey(key)) {
            put(key, value)
        } else {
            timeMap.remove(key)

            val date = Date()
            timeMap[key] = date.time

            get(key)
        }
    }

    fun set(key: K, value: V): V {
        check(isAlive) {
            initialize()
        }
        remove(key!!)
        put(key, value!!)

        return value
    }

    fun removeFromMap(key: K) {
        check(isAlive) {
            initialize()
        }
        if (contains(key!!)) {
            val value = remove(key)!!

            timeMap.remove(key)
            callback.onRemove(key, value)
        }
    }

    fun quitMap() {
        isAlive = false
        clear()
    }

    internal inner class CleanerThread : Thread() {
        override fun run() {
            while (isAlive) {
                cleanMap()
                try {
                    sleep(expireInMillis / 2)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        private fun cleanMap() {
            val currentTime: Long = Date().time
            for (key in timeMap.keys) {
                if (currentTime > timeMap[key]!! + expireInMillis) {
                    val value = remove(key!!)

                    timeMap.remove(key)
                    callback.onRemove(key, value)
                }
            }
        }

    }

    companion object {
        private const val serialVersionUID = 1L
    }

}