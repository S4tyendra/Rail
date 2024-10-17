package `in`.devh.rail.workers

import android.content.Context
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import `in`.devh.rail.CellInfoWrapper
import `in`.devh.rail.LocationWrapper
import `in`.devh.rail.data.models.CellLocationData
import `in`.devh.rail.database.Database
import `in`.devh.rail.functions.Logs.LogCollector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import kotlin.math.abs

private const val TAG = "CellLocationWorker"

class CellLocationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private val database = Database("CellLocation")

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val cellInfoWrapper = CellInfoWrapper(applicationContext)
            val locationWrapper = LocationWrapper(applicationContext)

            val cellData = cellInfoWrapper.getCellInfo()
            var location: Location? = null
            locationWrapper.getLocation { loc ->
                location = loc
            }

            // Wait for location to be available
            while (location == null) {
                kotlinx.coroutines.delay(100)
            }

            val newData = CellLocationData(
                mcc = cellData.mcc,
                mnc = cellData.mnc,
                lac = cellData.lac,
                cid = cellData.cid,
                longitude = location.longitude,
                latitude = location.latitude,
                timestamp = System.currentTimeMillis()
            )

            val lastData = database.get<CellLocationData>("lastData")

            if (lastData == null || hasCellTowerChanged(lastData, newData) || hasLocationChangedSignificantly(lastData, newData)) {
                database.set("lastData", newData)
                writeJsonToFile(newData)
                LogCollector.appendLog(TAG, "New data saved and written to file")
            } else {
                LogCollector.appendLog(TAG, "No significant changes detected")
            }

            Result.success()
        } catch (e: Exception) {
            LogCollector.appendLog(TAG, "Error in worker: ${e.message}")
            Result.failure()
        }
    }

    private fun hasCellTowerChanged(old: CellLocationData, new: CellLocationData): Boolean {
        return old.mcc != new.mcc || old.mnc != new.mnc || old.lac != new.lac || old.cid != new.cid
    }

    private fun hasLocationChangedSignificantly(old: CellLocationData, new: CellLocationData): Boolean {
        val threshold = 0.0001 // Adjust this value based on your needs
        return abs(old.latitude - new.latitude) > threshold || abs(old.longitude - new.longitude) > threshold
    }

    private fun writeJsonToFile(data: CellLocationData) {
        val json = JSONObject().apply {
            put("mcc", data.mcc)
            put("mnc", data.mnc)
            put("lac", data.lac)
            put("cid", data.cid)
            put("longitude", data.longitude)
            put("latitude", data.latitude)
            put("timestamp", data.timestamp)
        }.toString()

        val file = File(applicationContext.filesDir, "cell_location_data.json")
        FileWriter(file, true).use { it.write("$json\n") }
    }
}
