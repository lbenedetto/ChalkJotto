package com.benedetto.chalkjotto.game

import java.lang.IllegalStateException

data class GameState(
    var greenLetters: HashSet<String>,
    var yellowLetters: HashSet<String>,
    var redLetters: HashSet<String>,
    var guessedWords: HashSet<String>,
    var targetWord: String?,
    var wordDifficulty: Int,
    val wordLength: Int,
    var numSeconds: Long,
    var numGuesses: Long,
    var isGameOver: Boolean,
    var didWin: Boolean,
    var allowNewGuesses: Boolean
) {

    companion object {
        fun newGame(wordDifficulty: Int, wordLength: Int): GameState {
            return GameState(
                greenLetters = HashSet(),
                yellowLetters = HashSet(),
                redLetters = HashSet(),
                guessedWords = HashSet(),
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
    }

    fun createShareInstance(
        keepCurrentGuesses: Boolean,
        allowNewGuesses: Boolean
    ) : GameState {
        if (!allowNewGuesses && !keepCurrentGuesses) {
            throw IllegalStateException("Cannot discard guesses and block new guesses")
        }
        return GameState(
            greenLetters = HashSet(),
            yellowLetters = HashSet(),
            redLetters = HashSet(),
            guessedWords = if (keepCurrentGuesses) guessedWords else HashSet(),
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