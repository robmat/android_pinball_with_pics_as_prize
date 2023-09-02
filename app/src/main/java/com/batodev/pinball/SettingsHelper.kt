package com.batodev.pinball

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

class SettingsHelper(context: Context) {
    private val sharedPreferences: SharedPreferences
    val preferences: Preferences

    init {
        sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        preferences = loadPreferences()
    }

    fun savePreferences() {
        val editor = sharedPreferences.edit()
        editor.putString("uncoveredPics", preferences.uncoveredPics.joinToString(","))
        editor.apply()
    }

    private fun loadPreferences(): Preferences {
        val preferences = Preferences(mutableSetOf())
        preferences.uncoveredPics =
            sharedPreferences.getString("uncoveredPics", "")?.split(",")?.toMutableSet()
                ?: mutableSetOf()
        preferences.uncoveredPics = preferences.uncoveredPics.filter { it != "" }.toMutableSet()
        return preferences
    }
}

data class Preferences(
    var uncoveredPics: MutableSet<String>,
)