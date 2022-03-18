package com.putrash.common.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.putrash.common.toObject


object PreferencesHelper {

    fun init(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun SharedPreferences.remove(key: PreferencesKey) {
        edit().remove(key.toString()).apply()
    }

    fun SharedPreferences.clear() {
        edit().clear().apply()
    }

    inline operator fun <reified T>SharedPreferences.get(key: PreferencesKey, defaultValue: T): T {
        return when(T::class) {
            String::class -> getString(key.toString(), defaultValue as String) as T
            Int::class -> getInt(key.toString(), defaultValue as Int) as T
            Boolean::class -> getBoolean(key.toString(), defaultValue as Boolean) as T
            Float::class -> getFloat(key.toString(), defaultValue as Float) as T
            Long::class -> getLong(key.toString(), defaultValue as Long) as T
            else -> {
                val source = getString(key.toString(), "") ?: ""
                if (source.isNotEmpty()) {
                    source.toObject()
                } else {
                    defaultValue
                }
            }
        }
    }

    operator fun SharedPreferences.set(key: PreferencesKey, value: Any) {
        when(value) {
            is String -> edit { it.putString(key.toString(), value) }
            is Int -> edit { it.putInt(key.toString(), value) }
            is Boolean -> edit { it.putBoolean(key.toString(), value)}
            is Float -> edit { it. putFloat(key.toString(), value) }
            is Long -> edit { it.putLong(key.toString(), value) }
            else -> {
                edit {
                    val json = Gson().toJson(value)
                    it.putString(key.toString(), json)
                }
            }
        }
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }
}