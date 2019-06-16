package com.benedetto.chalkjotto.game

import android.content.res.Resources
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.Key
import com.benedetto.chalkjotto.definitions.random
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class GameModel(
        var keys: HashMap<Char, Key>,
        private var resources: Resources,
        wordDifficulty: Int,
        wordLength: Int) {
    var targetWord: String
    var validWords = HashSet<String>()
    var enteredWord = StringBuilder()
    var numSeconds = 0.toLong()
    var numGuesses = 0.toLong()
    var isGameOver = false

    init {
        val wordFile = resources.openRawResource(
                when (wordLength) {
                    4 -> {
                        addValidWords(R.raw.bottom_four, R.raw.middle_four, R.raw.top_four)
                        when (wordDifficulty) {
                            2 -> R.raw.bottom_four
                            1 -> R.raw.middle_four
                            else -> R.raw.top_four
                        }
                    }
                    6 -> {
                        addValidWords(R.raw.bottom_six, R.raw.middle_six, R.raw.top_six)
                        when (wordDifficulty) {
                            2 -> R.raw.bottom_six
                            1 -> R.raw.middle_six
                            else -> R.raw.top_six
                        }
                    }
                    7 -> {
                        addValidWords(R.raw.bottom_seven, R.raw.middle_seven, R.raw.top_seven)
                        when (wordDifficulty) {
                            2 -> R.raw.bottom_seven
                            1 -> R.raw.middle_seven
                            else -> R.raw.top_seven
                        }
                    }
                    else -> {
                        addValidWords(R.raw.bottom_five, R.raw.middle_five, R.raw.top_five)
                        when (wordDifficulty) {
                            2 -> R.raw.bottom_five
                            1 -> R.raw.middle_five
                            else -> R.raw.top_five
                        }
                    }
                }
        )
        val candidateWords = ArrayList<String>()
        BufferedReader(InputStreamReader(wordFile)).forEachLine { line ->
            candidateWords.add(line.toUpperCase())
        }
        targetWord = candidateWords[(0 until candidateWords.size).random()].split("\t")[0].toUpperCase()
    }

    private fun addValidWords(vararg ids: Int) {
        ids.forEach { id ->
            BufferedReader(InputStreamReader(resources.openRawResource(id))).forEachLine { line ->
                validWords.add(line.split("\t")[0].toUpperCase())
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
                    if (targetWord.contains(letter)) {
                        matchCount++
                    }
                }
        return matchCount
    }

    fun getOdds(): Int {
        return validWords.size
    }

}