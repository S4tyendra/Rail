package `in`.devh.rail.utils

import android.content.Context
import android.content.SharedPreferences
import `in`.devh.rail.data.models.AppContextProvider

object PreferenceManager {
    private const val PREF_NAME = "rail_preferences"
    private const val KEY_SERVICE_ENABLED = "service_enabled"


    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isServiceEnabled(context: Context = AppContextProvider.getAppContext()): Boolean {
        return getPreferences(context).getBoolean(KEY_SERVICE_ENABLED, false)
    }

    fun setServiceEnabled(context: Context = AppContextProvider.getAppContext(), enabled: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply()
    }
}
