package `in`.devh.rail.services


import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import cz.mroczis.netmonster.core.BuildConfig
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class LogcatService : Service() {
    private var logProcess: Process? = null
    private var logThread: Thread? = null

    override fun onCreate() {
        super.onCreate()
        startLogCapture()
    }
    

    private fun startLogCapture() {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH", Locale.US).format(Date())
            val logFile = File(getExternalFilesDir(null), "cellid_logs_$timestamp.txt")

            logProcess = Runtime.getRuntime().exec("logcat -f ${logFile.absolutePath} ${BuildConfig.LIBRARY_PACKAGE_NAME}:V *:S")

            logThread = Thread {
                val reader = BufferedReader(InputStreamReader(logProcess!!.inputStream))
                val writer = FileWriter(logFile)
                val buffer = CharArray(1024)
                var bytesRead: Int
                while (reader.read(buffer).also { bytesRead = it } != -1) {
                    writer.write(buffer, 0, bytesRead)
                }
                writer.close()
            }.apply { start() }

        } catch (e: Exception) {
            Log.e("LogService", "Failed to start logging", e)
        }
    }


    override fun onDestroy() {
        logProcess?.destroy()
        logThread?.interrupt()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}