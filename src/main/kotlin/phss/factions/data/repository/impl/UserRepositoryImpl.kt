package phss.factions.data.repository.impl

import phss.factions.data.dao.DataDao
import phss.factions.data.domain.User
import phss.factions.data.repository.DataRepository
import java.util.*

class UserRepositoryImpl(
    private val userDao: DataDao<UUID, User>
) : DataRepository<UUID, User> {

    override val data: List<User>
        get() = ArrayList(userDao.data.values)

    override fun get(query: UUID): User? {
        return userDao.data[query]
    }

    override fun create(data: User) {
        userDao.create(data)
    }

    override fun edit(data: User) {
        userDao.save(data)
    }

    override fun delete(data: User) {
        userDao.delete(data)
    }

}