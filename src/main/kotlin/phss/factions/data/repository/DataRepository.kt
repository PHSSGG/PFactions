package phss.factions.data.repository

interface DataRepository<R, T> {

    val data: List<T>

    operator fun get(query: R): T?
    fun create(data: T)
    fun edit(data: T)
    fun delete(data: T)

}