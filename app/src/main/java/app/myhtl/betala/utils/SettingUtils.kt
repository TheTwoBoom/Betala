package app.myhtl.betala.utils

import android.app.Activity
import android.content.Context

/**
 * Utils for managing settings/prefs
 *
 * Supported types of value:
 * Boolean, String, Int
 */
class SettingUtils(val context: Context) {
    private var pref = (context as? Activity)?.getPreferences(Context.MODE_PRIVATE)
    private var editor = pref?.edit()
    fun contains(id: String) = pref?.contains(id)
    fun setBool(id: String, value: Boolean) { editor?.putBoolean(id, value)?.apply() }
    fun getBool(id: String) = pref?.getBoolean(id, false)
    fun delete(id: String) { editor?.remove(id)?.apply() }
}