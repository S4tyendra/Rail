package `in`.devh.rail.database

import `in`.devh.rail.data.models.AppContextProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class Database(private val databaseName: String) {
    private val context = AppContextProvider.getAppContext()
    val gson = Gson()
    val file: File

    init {
        val dir = context.filesDir
        file = File(dir, "$databaseName.json")
        if (!file.exists()) {
            file.createNewFile()
            file.writeText("{}")
        }
    }

    inline fun <reified T> get(key: Any, default: T? = null): T? {
        val json = file.readText()
        val type = object : TypeToken<Map<String, T>>() {}.type
        val data: Map<String, T> = gson.fromJson(json, type) ?: emptyMap()
        return data[key.toString()] ?: default
    }

    fun <T> set(key: Any, value: T?) {
        val json = file.readText()
        val type = object : TypeToken<MutableMap<String, Any>>() {}.type
        val data: MutableMap<String, Any> = gson.fromJson(json, type) ?: mutableMapOf()

        if (value == null) {
            data.remove(key.toString())
        } else {
            data[key.toString()] = value
        }

        file.writeText(gson.toJson(data))
    }

    fun remove(key: Any) {
        set(key, null)
    }

    fun clear() {
        file.writeText("{}")
    }
}