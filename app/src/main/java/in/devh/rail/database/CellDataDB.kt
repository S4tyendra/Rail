package `in`.devh.rail.database
import android.content.Context
import android.content.SharedPreferences
import `in`.devh.rail.data.models.AppContextProvider
import org.json.JSONObject

import java.io.File

data class CellDataDB(
    private val fileName: String = "cell_data.txt"
) {
    private val sharedPref: SharedPreferences by lazy {
        AppContextProvider.getAppContext().getSharedPreferences("cell_data", Context.MODE_PRIVATE)
    }

    private var dataFile: File? = null

    private fun getDataFilePath(): String {
        return sharedPref.getString("dataFilePath", "Data file not initialized") ?: "Data file not initialized"
    }

    fun init() {
        val dataDir = AppContextProvider.getAppContext().filesDir
        dataFile = File(dataDir, fileName)
        val filePath = "${dataFile?.absolutePath}"

        with(sharedPref.edit()) {
            putString("dataFilePath", filePath)
            apply()
        }

        if (dataFile?.exists() == false) {
            dataFile?.createNewFile()
        }
    }

    fun store(mcc: String, mnc: String, lac: String, cid: String) {
        if (dataFile == null) {
            init()
        }

        val key = "${mcc}_${mnc}_${lac}_${cid}"
        val lines = dataFile?.readLines() ?: emptyList()

        // Check if key already exists
        val keyExists = lines.any { line ->
            try {
                val json = JSONObject(line)
                json.has(key)
            } catch (e: Exception) {
                false
            }
        }

        if (!keyExists) {
            val newEntry = JSONObject().apply {
                put(key, false)
            }
            dataFile?.appendText("${newEntry}\n")
        }
    }

    fun getFalse(): List<String> {
        if (dataFile == null) {
            init()
        }

        val falseKeys = mutableListOf<String>()
        dataFile?.readLines()?.forEach { line ->
            try {
                val json = JSONObject(line)
                json.keys().forEach { key ->
                    if (!json.getBoolean(key)) {
                        falseKeys.add(key)
                    }
                }
            } catch (e: Exception) {
                // Skip invalid lines
            }
        }
        return falseKeys
    }

    fun setTrue(key: String) {
        if (dataFile == null) {
            init()
        }

        val lines = dataFile?.readLines() ?: return
        val updatedLines = lines.map { line ->
            try {
                val json = JSONObject(line)
                if (json.has(key) && !json.getBoolean(key)) {
                    JSONObject().apply {
                        put(key, true)
                    }.toString()
                } else {
                    line
                }
            } catch (e: Exception) {
                line
            }
        }

        dataFile?.writeText(updatedLines.joinToString("\n", postfix = "\n"))
    }

    fun getAll(): List<String> {
        if (dataFile == null) {
            init()
        }

        return dataFile?.readLines() ?: emptyList()
    }
}