package phss.factions.data.domain

import phss.factions.data.dao.DataDao

interface GenericData<R, T> {

    val dao: DataDao<R, T>
    val data: T

}