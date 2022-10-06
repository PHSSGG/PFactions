package phss.factions.data.dao

interface DataDao<R, T> {

    fun load(): HashMap<R, T>
    val data: HashMap<R, T>

    fun create(data: T)
    fun delete(data: T)

    fun save(data: T)
    fun saveAll()

    fun getSQLQuery(data: T): String
    fun getSQLInsert(data: T): String
    fun getSQLUpdate(data: T): String
    fun getSQLDelete(data: T): String

}