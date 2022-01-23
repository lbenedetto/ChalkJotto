package com.benedetto.chalkjotto.game

import com.google.android.gms.common.util.Base64Utils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class GameState(
    @SerialName("y")
    var greenLetters: HashSet<String>,
    @SerialName("m")
    var yellowLetters: HashSet<String>,
    @SerialName("n")
    var redLetters: HashSet<String>,
    @SerialName("w")
    var guessedWords: MutableList<String>,
    @SerialName("t")
    var targetWord: String?,
    @SerialName("d")
    var wordDifficulty: Int,
    @SerialName("l")
    val wordLength: Int,
    @SerialName("s")
    var numSeconds: Long,
    @SerialName("ng")
    var numGuesses: Long,
    @SerialName("o")
    var isGameOver: Boolean,
    @SerialName("dw")
    var didWin: Boolean,
    @SerialName("a")
    var allowNewGuesses: Boolean,
    @Transient
    var allowSettingRecords: Boolean = true
) {

    /**
     * Sanitize a saved game state for use as challenge
     */
    fun sanitize() {
        didWin = false
        isGameOver = false
        numSeconds = 0
        numGuesses = 0
    }

    companion object {
        fun newGame(wordDifficulty: Int, wordLength: Int): GameState {
            return GameState(
                greenLetters = HashSet(),
                yellowLetters = HashSet(),
                redLetters = HashSet(),
                guessedWords = ArrayList(),
                targetWord = null,
                wordDifficulty = wordDifficulty,
                wordLength = wordLength,
                numSeconds = 0,
                numGuesses = 0,
                isGameOver = false,
                didWin = false,
                allowNewGuesses = true
            )
        }

        fun fromPayload(payload: String): GameState? {
            return try {
                val json = Base64Utils.decodeUrlSafe(payload).decodeToString()
                Json.decodeFromString(json)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }

    fun toPayload() : String {
        val jsonShareState = Json.encodeToString(this)
        return Base64Utils.encodeUrlSafe(jsonShareState.toByteArray())
    }

    fun createShareInstance(
        shareGuesses: Boolean,
        shareDeductions: Boolean,
        allowNewGuesses: Boolean
    ) : GameState {
        if (!allowNewGuesses && !shareGuesses && !shareDeductions) {
            throw IllegalStateException("Cannot discard guesses and block new guesses")
        }
        return GameState(
            greenLetters = if (shareDeductions) greenLetters else HashSet(),
            yellowLetters = if (shareDeductions) yellowLetters else HashSet(),
            redLetters = if (shareDeductions) redLetters else HashSet(),
            guessedWords = if (shareGuesses) guessedWords else ArrayList(),
            targetWord = targetWord,
            wordDifficulty = wordDifficulty,
            wordLength = wordLength,
            numSeconds = 0,
            numGuesses = 0,
            isGameOver = false,
            didWin = false,
            allowNewGuesses = allowNewGuesses
        )
    }
}