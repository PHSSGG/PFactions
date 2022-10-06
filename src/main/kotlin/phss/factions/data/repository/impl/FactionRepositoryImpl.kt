package phss.factions.data.repository.impl

import phss.factions.data.dao.DataDao
import phss.factions.data.domain.Faction
import phss.factions.data.repository.DataRepository

class FactionRepositoryImpl(
    private val factionDao: DataDao<Int, Faction>
) : DataRepository<Int, Faction> {

    override val data: List<Faction>
        get() = ArrayList(factionDao.data.values)

    override fun get(query: Int): Faction? {
        return factionDao.data[query]
    }

    override fun create(data: Faction) {
        factionDao.create(data)
    }

    override fun edit(data: Faction) {
        factionDao.save(data)
    }

    override fun delete(data: Faction) {
        factionDao.delete(data)
    }

}