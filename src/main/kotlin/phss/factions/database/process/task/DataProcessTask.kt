package phss.factions.database.process.task

import phss.factions.data.domain.GenericData
import phss.factions.database.process.DataProcess
import java.util.concurrent.LinkedBlockingQueue

class DataProcessTask(
    private val dataProcess: DataProcess
) : Runnable {

    var queue = LinkedBlockingQueue<Pair<GenericData<Any, Any>, Boolean>>()
    var running = false

    override fun run() {
        while (running) {
            val (data, toSave) = queue.take()
            if (toSave) dataProcess.save(data).join() else dataProcess.delete(data).join()

            if (queue.isEmpty()) running = false
        }
    }

    companion object {
        fun startTask(task: DataProcessTask) {
            if (task.running) return

            val thread = Thread(task)
            task.running = true

            thread.start()
        }

        fun stopTask(task: DataProcessTask) {
            task.running = false
        }
    }

}