import android.util.Log
import `in`.devh.rail.database.Database
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

    val database = Database("cell_tower_logs")
    val savedData: Map<String, Any> = database.getAll()

    // Filter items with false values and convert to list
    val pendingItems = savedData.filter { it.value == false }.keys.toList()

    // Process items concurrently but with a limit
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
                                database.set(cellInfo, true)
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