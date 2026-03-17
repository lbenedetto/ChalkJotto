package com.benedetto.chalkjotto.definitions

import android.content.Context
import android.content.SharedPreferences
import com.benedetto.chalkjotto.game.GameState
import com.google.firebase.crashlytics.FirebaseCrashlytics
import androidx.core.content.edit

object DataManager {
    private lateinit var prefs: SharedPreferences
    private const val PREFERRED_WORD_LENGTH = "wordLength"
    private const val PREFERRED_DIFFICULTY = "difficulty"
    private const val ACTIVE_LESSON = "activeLesson"
    private const val HAS_SEEN_TUTORIAL = "hasSeenTutorial"
    private const val HAS_SEEN_RATING_PROMPT = "hasSeenRatingPrompt"
    private const val VIBRATION_ENABLED = "vibrationEnabled"
    private const val SOUND_ENABLED = "soundEnabled"
    private const val HIGH_CONTRAST_MODE_ENABLED = "highContrastModeEnabled"
    private const val ASSISTANCE_ENABLED = "assistanceEnabled"
    private const val GAME_STATE_GREEN_LETTERS = "gameState_greenLetters"
    private const val GAME_STATE_YELLOW_LETTERS = "gameState_yellowLetters"
    private const val GAME_STATE_BLUE_LETTERS = "gameState_blueLetters"
    private const val GAME_STATE_PINK_LETTERS = "gameState_pinkLetters"
    private const val GAME_STATE_RED_LETTERS = "gameState_redLetters"
    private const val GAME_STATE_GUESSED_WORDS = "gameState_guessedWords"
    private const val GAME_STATE_TARGET_WORD = "gameState_targetWord"
    private const val GAME_STATE_WORD_DIFFICULTY = "gameState_wordDifficulty"
    private const val GAME_STATE_WORD_LENGTH = "gameState_wordLength"
    private const val GAME_STATE_NUM_SECONDS = "gameState_numSeconds"
    private const val GAME_STATE_NUM_GUESSES = "gameState_numGuesses"
    private const val GAME_STATE_IS_GAME_OVER = "gameState_isGameOver"
    private const val GAME_STATE_DID_WIN = "gameState_didWin"
    private const val GAME_STATE_ALLOW_NEW_GUESSES = "gameState_allowNewGuesses"
    private const val ANALYTICS_ENABLED = "analyticsEnabled"
    private const val ANALYTICS_CONSENT_SHOWN = "analyticsConsentShown"

    var initialized = false

    fun init(context: Context) {
        if (initialized) {
            return
        }
        prefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        initialized = true
    }

    var hasSeenTutorial: Boolean
        get() = prefs.getBoolean(HAS_SEEN_TUTORIAL, false)
        set(value) = put(HAS_SEEN_TUTORIAL, value)

    var wordLength: Int
        get() = prefs.getInt(PREFERRED_WORD_LENGTH, 5)
        set(value) = put(PREFERRED_WORD_LENGTH, value)

    var difficulty: Int
        get() = prefs.getInt(PREFERRED_DIFFICULTY, 0)
        set(value) = put(PREFERRED_DIFFICULTY, value)

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(VIBRATION_ENABLED, true)
        set(value) = put(VIBRATION_ENABLED, value)

    var soundEnabled: Boolean
        get() = prefs.getBoolean(SOUND_ENABLED, true)
        set(value) = put(SOUND_ENABLED, value)

    var highContrastModeEnabled: Boolean
        get() = prefs.getBoolean(HIGH_CONTRAST_MODE_ENABLED, false)
        set(value) = put(HIGH_CONTRAST_MODE_ENABLED, value)

    var assistance: Boolean
        get() = prefs.getBoolean(ASSISTANCE_ENABLED, true)
        set(value) = put(ASSISTANCE_ENABLED, value)

    var hasSeenRatingPrompt: Boolean
        get() = prefs.getBoolean(HAS_SEEN_RATING_PROMPT, false)
        set(value) = put(HAS_SEEN_RATING_PROMPT, value)

    var analyticsEnabled: Boolean
        get() = prefs.getBoolean(ANALYTICS_ENABLED, false)
        set(value) = put(ANALYTICS_ENABLED, value)

    var analyticsConsentShown: Boolean
        get() = prefs.getBoolean(ANALYTICS_CONSENT_SHOWN, false)
        set(value) = put(ANALYTICS_CONSENT_SHOWN, value)

    var activeLesson: String?
        get() = prefs.getString(ACTIVE_LESSON, null)
        set(value) = put(ACTIVE_LESSON, value)

    fun hasCompletedLesson(lesson: String): Boolean {
        return prefs.getBoolean("LESSON_$lesson", false)
    }

    fun setCompletedLesson(lesson: String) {
        put("LESSON_$lesson", true)
    }

    var isGameInProgress: Boolean
        get() {
            return !prefs.getBoolean(GAME_STATE_IS_GAME_OVER, true)
        }
        set(value) = put(GAME_STATE_IS_GAME_OVER, !value)

    var gameState: GameState?
        get() {
            try {
                val targetWord = prefs.getString(GAME_STATE_TARGET_WORD, null) ?: return null
                val greenLetters =
                    prefs.getStringSet(GAME_STATE_GREEN_LETTERS, HashSet()) as HashSet<String>
                val yellowLetters =
                    prefs.getStringSet(GAME_STATE_YELLOW_LETTERS, HashSet()) as HashSet<String>
                val blueLetters =
                    prefs.getStringSet(GAME_STATE_BLUE_LETTERS, HashSet()) as HashSet<String>
                val pinkLetters =
                    prefs.getStringSet(GAME_STATE_PINK_LETTERS, HashSet()) as HashSet<String>
                val redLetters =
                    prefs.getStringSet(GAME_STATE_RED_LETTERS, HashSet()) as HashSet<String>
                val guessedWords = prefs.getString(GAME_STATE_GUESSED_WORDS, "") ?: ""
                val wordDifficulty = prefs.getInt(GAME_STATE_WORD_DIFFICULTY, 0)
                val wordLength = prefs.getInt(GAME_STATE_WORD_LENGTH, 5)
                val numSeconds = prefs.getLong(GAME_STATE_NUM_SECONDS, 0)
                val numGuesses = prefs.getLong(GAME_STATE_NUM_GUESSES, 0)
                val isGameOver = prefs.getBoolean(GAME_STATE_IS_GAME_OVER, false)
                val didWin = prefs.getBoolean(GAME_STATE_DID_WIN, false)
                val allowNewGuesses = prefs.getBoolean(GAME_STATE_ALLOW_NEW_GUESSES, true)

                return GameState(
                    greenLetters = greenLetters,
                    yellowLetters = yellowLetters,
                    blueLetters = blueLetters,
                    pinkLetters = pinkLetters,
                    redLetters = redLetters,
                    guessedWords = guessedWords.split(",")
                        .filter { it.isNotBlank() }
                        .toMutableList(),
                    targetWord = targetWord,
                    wordDifficulty = wordDifficulty,
                    wordLength = wordLength,
                    numSeconds = numSeconds,
                    numGuesses = numGuesses,
                    isGameOver = isGameOver,
                    didWin = didWin,
                    allowNewGuesses = allowNewGuesses
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
        set(value) = prefs.edit {
            putString(GAME_STATE_TARGET_WORD, value?.targetWord)
            if (value != null) {
                putStringSet(GAME_STATE_GREEN_LETTERS, value.greenLetters)
                putStringSet(GAME_STATE_YELLOW_LETTERS, value.yellowLetters)
                putStringSet(GAME_STATE_BLUE_LETTERS, value.blueLetters)
                putStringSet(GAME_STATE_PINK_LETTERS, value.pinkLetters)
                putStringSet(GAME_STATE_RED_LETTERS, value.redLetters)
                putString(GAME_STATE_GUESSED_WORDS, value.guessedWords.joinToString(","))
                putInt(GAME_STATE_WORD_DIFFICULTY, value.wordDifficulty)
                putInt(GAME_STATE_WORD_LENGTH, value.wordLength)
                putLong(GAME_STATE_NUM_SECONDS, value.numSeconds)
                putLong(GAME_STATE_NUM_GUESSES, value.numGuesses)
                putBoolean(GAME_STATE_IS_GAME_OVER, value.isGameOver)
                putBoolean(GAME_STATE_DID_WIN, value.didWin)
                putBoolean(GAME_STATE_ALLOW_NEW_GUESSES, value.allowNewGuesses)
            }
        }


    private fun put(key: String, value: Any?) = prefs.edit {
        when (value) {
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is String -> putString(key, value)
        }
    }
}