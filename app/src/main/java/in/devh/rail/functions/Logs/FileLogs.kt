package `in`.devh.rail.functions.Logs

import android.util.Log
import `in`.devh.rail.data.models.LogsData


object LogCollector {
    var logs = StringBuilder()

    fun appendLog(tag: String, message: String) {
        logs.append("$tag: $message\n")
        val logsData = LogsData()
        logsData.writeLog(tag, message)


    }

    fun getLogs(): String {
        return logs.toString()
    }

    fun clearLogs() {
        logs.clear()
    }
}

fun logD(tag: String, message: String) {
    Log.d(tag, message)
    LogCollector.appendLog(tag, message)
}