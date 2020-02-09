package com.benedetto.chalkjotto.definitions

import android.content.Context
import android.content.SharedPreferences

object DataManager {
    private lateinit var prefs: SharedPreferences
    private const val PREFERRED_WORD_LENGTH = "wordLength"
    private const val PREFERRED_DIFFICULTY = "difficulty"
    private const val HAS_SEEN_TUTORIAL = "hasSeenTutorial"
    private const val VIBRATION_ENABLED = "vibrationEnabled"
    private const val SOUND_ENABLED = "soundEnabled"
    private const val FEWEST_GUESSES = "fewestGuesses"
    private const val FASTEST_TIME = "fastestTime"

    fun init(context: Context) {
        prefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
    }

    var hasSeenTutoral: Boolean
        get() = get("hasSeenTutorial", false) as Boolean
        set(value) = put(HAS_SEEN_TUTORIAL, value)

    var wordLength: Int
        get() = get(PREFERRED_WORD_LENGTH, 5) as Int
        set(value) = put(PREFERRED_WORD_LENGTH, value)

    var difficulty: Int
        get() = get(PREFERRED_DIFFICULTY, 0) as Int
        set(value) = put(PREFERRED_DIFFICULTY, value)

    var vibrationEnabled: Boolean
        get() = get(VIBRATION_ENABLED, true) as Boolean
        set(value) = put(VIBRATION_ENABLED, value)

    var soundEnabled: Boolean
        get() = get(SOUND_ENABLED, true) as Boolean
        set(value) = put(SOUND_ENABLED, value)

    var fewestGuesses: Long?
        get() {
            val value = get("${FEWEST_GUESSES}_${difficulty}_$wordLength", -1L) as Long
            if (value == -1L) return null
            return value
        }
        set(value) = put("${FEWEST_GUESSES}_${difficulty}_$wordLength", value ?: -1L)

    var fastestTimeSeconds: Long?
        get() {
            val value = get("${FASTEST_TIME}_${difficulty}_$wordLength", -1L) as Long
            if (value == -1L) return null
            return value
        }
        set(value) = put("${FASTEST_TIME}_${difficulty}_$wordLength", value ?: -1L)

    private fun put(key: String, value: Any?) {
        val editor = prefs.edit()
        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
        }
        editor.apply()
    }

    private fun get(key: String, default: Any): Any {
        return when (default) {
            is Boolean -> prefs.getBoolean(key, default)
            is Int -> prefs.getInt(key, default)
            is Long -> prefs.getLong(key, default)
            else -> default
        }
    }
}