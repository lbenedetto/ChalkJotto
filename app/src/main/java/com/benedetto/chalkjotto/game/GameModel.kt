package com.benedetto.chalkjotto.game

import android.content.res.Resources
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.Key
import com.benedetto.chalkjotto.definitions.KeyState
import java.io.BufferedReader
import java.io.InputStreamReader

class GameModel(
        var keys: HashMap<Char, Key>,
        private var resources: Resources,
        var gameState: GameState) {
    var validWords = HashSet<String>()
    var enteredWord = StringBuilder()

    var numSeconds: Long
        get() = gameState.numSeconds
        set(value) {
            gameState.numSeconds = value
        }

    var numGuesses: Long
        get() = gameState.numGuesses
        set(value) {
            gameState.numGuesses = value
        }

    var isGameOver: Boolean
        get() = gameState.isGameOver
        set(value) {
            gameState.isGameOver = value
        }

    var targetWord: String?
        get() = gameState.targetWord
        set(value) {
            gameState.targetWord = value
        }

    init {
        if (targetWord == null) {
            targetWord = pickTargetWord()
        }

        when (gameState.wordLength) {
            4 -> addValidWords(R.raw.bottom_four, R.raw.middle_four, R.raw.top_four)
            6 -> addValidWords(R.raw.bottom_six, R.raw.middle_six, R.raw.top_six)
            7 -> addValidWords(R.raw.bottom_seven, R.raw.middle_seven, R.raw.top_seven)
            else -> addValidWords(R.raw.bottom_five, R.raw.middle_five, R.raw.top_five)
        }
    }

    private fun pickTargetWord() : String {
        val wordFile = resources.openRawResource(
            when (gameState.wordLength) {
                4 -> {
                    addValidWords(R.raw.bottom_four, R.raw.middle_four, R.raw.top_four)
                    when (gameState.wordDifficulty) {
                        2 -> R.raw.bottom_four
                        1 -> R.raw.middle_four
                        else -> R.raw.top_four
                    }
                }
                6 -> {
                    addValidWords(R.raw.bottom_six, R.raw.middle_six, R.raw.top_six)
                    when (gameState.wordDifficulty) {
                        2 -> R.raw.bottom_six
                        1 -> R.raw.middle_six
                        else -> R.raw.top_six
                    }
                }
                7 -> {
                    addValidWords(R.raw.bottom_seven, R.raw.middle_seven, R.raw.top_seven)
                    when (gameState.wordDifficulty) {
                        2 -> R.raw.bottom_seven
                        1 -> R.raw.middle_seven
                        else -> R.raw.top_seven
                    }
                }
                else -> {
                    addValidWords(R.raw.bottom_five, R.raw.middle_five, R.raw.top_five)
                    when (gameState.wordDifficulty) {
                        2 -> R.raw.bottom_five
                        1 -> R.raw.middle_five
                        else -> R.raw.top_five
                    }
                }
            }
        )
        val candidateWords = ArrayList<String>()
        BufferedReader(InputStreamReader(wordFile)).forEachLine { line ->
            val word = line.split("\t")[0].uppercase()
            if (word.toCharArray().distinct().size == gameState.wordLength) {
                candidateWords.add(word)
            }
        }
        return candidateWords.random()
    }

    fun updateState(key: Key, newState: KeyState) {
        when(key.state) {
            KeyState.MAYBE -> gameState.yellowLetters.remove(key.letter)
            KeyState.MAYBE_BLUE -> gameState.blueLetters.remove(key.letter)
            KeyState.MAYBE_PINK -> gameState.pinkLetters.remove(key.letter)
            KeyState.YES -> gameState.greenLetters.remove(key.letter)
            KeyState.NO -> gameState.redLetters.remove(key.letter)
            KeyState.BLANK -> {} // Do nothing
        }

        when(newState) {
            KeyState.MAYBE -> gameState.yellowLetters.add(key.letter)
            KeyState.MAYBE_BLUE -> gameState.blueLetters.add(key.letter)
            KeyState.MAYBE_PINK -> gameState.pinkLetters.add(key.letter)
            KeyState.YES -> gameState.greenLetters.add(key.letter)
            KeyState.NO -> gameState.redLetters.add(key.letter)
            KeyState.BLANK -> {} // Do nothing
        }

        key.updateState(newState)
    }

    private fun addValidWords(vararg ids: Int) {
        ids.forEach { id ->
            BufferedReader(InputStreamReader(resources.openRawResource(id))).forEachLine { line ->
                validWords.add(line.split("\t")[0].uppercase())
            }
        }
    }

    fun clearEnteredWord() {
        enteredWord = StringBuilder()
    }

    fun getNumberOfMatchingLetters(guess: String): Int {
        var matchCount = 0
        guess.toCharArray()
                .distinct()
                .forEach { letter ->
                    if (targetWord!!.contains(letter)) {
                        matchCount++
                    }
                }
        return matchCount
    }

    fun isAnagram(guess: String): Boolean {
        val targetSorted = targetWord!!.toCharArray()
        targetSorted.sort()
        val guessSorted = guess.toCharArray()
        guessSorted.sort()
        return targetSorted.contentEquals(guessSorted)
    }
}