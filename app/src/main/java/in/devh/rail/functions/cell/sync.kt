import android.util.Log
import `in`.devh.rail.database.CellDataDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

suspend fun fetchCoordinates() {
    val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .build()

    val db = CellDataDB()
    // Get all cell IDs that have false value
    val pendingItems = db.getFalse()

    // Process items concurrently with a limit
    coroutineScope {
        pendingItems.map { cellInfo ->
            async {
                try {
                    val request = Request.Builder()
                        .url("https://cell.devh.in/coordinates?cellinfo=$cellInfo")
                        .build()

                    withContext(Dispatchers.IO) {
                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                // Update the value to true if request was successful
                                db.setTrue(cellInfo)
                                Log.d("FetchCoordinates", "Successfully processed $cellInfo")
                            } else {
                                Log.w("FetchCoordinates", "Failed to fetch coordinates for $cellInfo: ${response.code}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FetchCoordinates", "Error processing $cellInfo: ${e.message}")
                }
            }
        }.awaitAll()
    }
}