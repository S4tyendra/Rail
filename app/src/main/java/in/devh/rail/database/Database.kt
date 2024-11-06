package `in`.devh.rail.database

import `in`.devh.rail.data.models.AppContextProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import android.util.Log // For Android logging

class Database(private val databaseName: String) {
    private val context = AppContextProvider.getAppContext()
    val gson = Gson()
    val file: File

    init {
        val dir = context.filesDir
        file = File(dir, "$databaseName.json")
        if (!file.exists()) {
            try {
                file.createNewFile()
                file.writeText("{}")
            } catch (e: Exception) {
                Log.e("Database", "Error creating file: ${e.message}")
            }
        }
    }

    inline fun <reified T> get(key: Any, default: T? = null): T? {
        return try {
            val json = file.readText()
            val type = object : TypeToken<Map<String, T>>() {}.type
            val data: Map<String, T> = gson.fromJson(json, type) ?: emptyMap()
            data[key.toString()] ?: default
        } catch (e: Exception) {
            Log.e("Database", "Error reading key $key: ${e.message}")
            default
        }
    }

    fun <T> getAll(): Map<String, T> {
        return try {
            val json = file.readText()
            val type = object : TypeToken<Map<String, T>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            Log.e("Database", "Error reading all data: ${e.message}")
            emptyMap()
        }
    }

    fun <T> set(key: Any, value: T?, allowDuplicates: Boolean = true) {
        try {
            val json = file.readText()
            val type = object : TypeToken<MutableMap<String, Any>>() {}.type
            val data: MutableMap<String, Any> = gson.fromJson(json, type) ?: mutableMapOf()
            if (data.containsKey(key.toString()) && !allowDuplicates) {
                return
            }

            if (value == null) {
                data.remove(key.toString())
            } else {
                data[key.toString()] = value
            }

            file.writeText(gson.toJson(data))
        } catch (e: Exception) {
            Log.e("Database", "Error setting key $key: ${e.message}")
        }
    }

    fun remove(key: Any) {
        set(key, null)
    }

    fun clear() {
        try {
            file.writeText("{}")
        } catch (e: Exception) {
            Log.e("Database", "Error clearing database: ${e.message}")
        }
    }
}
