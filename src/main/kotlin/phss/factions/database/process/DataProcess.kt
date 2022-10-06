package phss.factions.database.process

import phss.factions.data.domain.GenericData
import phss.factions.database.controller.DataController
import phss.factions.database.process.task.DataProcessTask
import java.util.concurrent.CompletableFuture

class DataProcess(
    private val controller: DataController
) {

    val task = DataProcessTask(this)

    fun save(data: GenericData<Any, Any>): CompletableFuture<GenericData<Any, Any>> {
        return CompletableFuture.supplyAsync {
            controller.saveData(data)
            data
        }
    }

    fun delete(data: GenericData<Any, Any>): CompletableFuture<GenericData<Any, Any>> {
        return CompletableFuture.supplyAsync {
            controller.deleteData(data)
            data
        }
    }

}