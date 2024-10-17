package `in`.devh.rail.data.models
import android.content.Context
import android.content.SharedPreferences

import java.io.File

object AppContextProvider {
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun getAppContext(): Context {
        if (!::appContext.isInitialized) {
            throw IllegalStateException("AppContextProvider is not initialized. Call initialize() first.")
        }
        return appContext
    }
}


data class LogsData(
    var logFileName: String = "app_log.txt"
) {
    private val sharedPref: SharedPreferences by lazy {
        AppContextProvider.getAppContext().getSharedPreferences("rail", Context.MODE_PRIVATE)
    }

    private var logFile: File? = null

    fun getLogFilePath(): String {
        return sharedPref.getString("logFilePath", "Log file not initialized") ?: "Log file not initialized"
    }

    fun initializeLogFile() {
        val dataDir = AppContextProvider.getAppContext().filesDir
        logFile = File(dataDir, logFileName)
        val filePath = "${logFile?.absolutePath}"

        with(sharedPref.edit()) {
            putString("logFilePath", filePath)
            apply()
        }

        if (logFile?.exists() == false) {
            logFile?.createNewFile()
        }
    }

    fun writeLog(tag: String, message: String) {
        if (logFile == null) {
            initializeLogFile()
        }
        println("$tag : $message")
        println("Log file path : ${getLogFilePath()}")
        println("Log file exists : ${logFile?.exists()}")
        logFile?.appendText("$tag : $message\n")
    }

    fun readLog(): String {
        return logFile?.readText() ?: "Log file not initialized"
    }
}