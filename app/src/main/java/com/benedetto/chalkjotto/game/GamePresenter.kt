package com.benedetto.chalkjotto.game

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.benedetto.chalkjotto.PauseActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.databinding.GuessItemBinding
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.definitions.Sound.penClickSound
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.dialogs.GameOverDialog
import com.benedetto.chalkjotto.dialogs.showColorPickerDialog

@SuppressLint("InflateParams", "ClickableViewAccessibility")
class GamePresenter(private val model: GameModel, val view: GameActivity) {
    private var mTimer: Runnable
    private var mHandler = Handler(Looper.getMainLooper())
    private var mIsRunning = false
    private var showPauseScreenOnResume = false

    init {
        mTimer = object : Runnable {
            override fun run() {
                model.numSeconds++
                view.setTimer(model.numSeconds)
                // Save game every 10 seconds to recover from crash
                if (model.numSeconds % 10 == 0L) {
                    DataManager.gameState = model.gameState
                }
                if (mIsRunning) mHandler.postDelayed(this, 1000)
            }
        }
        view.refillUserInputFieldWithTiles()

        model.gameState.guessedWords.forEach { word ->
            displayGuessedWord(word, false)
        }

        model.keys.forEach { (character, key) ->
            when {
                model.gameState.redLetters.contains("$character") -> {
                    key.updateState(KeyState.NO)
                }
                model.gameState.greenLetters.contains("$character") -> {
                    key.updateState(KeyState.YES)
                }
                model.gameState.yellowLetters.contains("$character") -> {
                    key.updateState(KeyState.MAYBE)
                }
            }

            key.view.setOnTouchListener(ScaleOnTouch)
            key.view.setOnClickListener {
                tapSound()
                vibrate()
                if (model.enteredWord.length < DataManager.wordLength) {
                    val letter = view.binding.layoutInputGuessWord.getChildAt(model.enteredWord.length) as TextView
                    letter.setOnClickListener {
                        showColorPickerDialog(view, key, model)
                    }
                    letter.text = key.letter
                    letter.setBackgroundResource(key.state.background)
                    model.enteredWord.append(letter.text.toString())
                    key.addListener(letter)
                }
            }

            key.view.setOnLongClickListener {
                showColorPickerDialog(view, key, model)
                true
            }
        }

        view.setNumberOfGuesses(model.numGuesses)
        view.setTimer(model.numSeconds)
    }

    fun submitButtonPressed() {
        if (model.enteredWord.length == DataManager.wordLength) {
            val guess = model.enteredWord.toString()
            val isValid = model.validWords.contains(guess.uppercase())
            if (isValid) {
                model.numGuesses++
                if (guess == model.targetWord) {
                    view.refillUserInputFieldWithTiles()
                    model.isGameOver = true
                    pause()
                    exitToTitle(didWin = true)
                } else {
                    model.gameState.guessedWords.add(guess)

                    val guessHolder = displayGuessedWord(guess, true)

                    view.moveWordToView(guessHolder.layoutGuessedWord)
                    view.refillUserInputFieldWithTiles()
                    view.setNumberOfGuesses(model.numGuesses)
                    model.clearEnteredWord()
                }
            } else {
                view.binding.layoutInputGuessWord.clearAnimation()
                view.binding.layoutInputGuessWord.startAnimation(AnimationUtils.loadAnimation(view, R.anim.shake))
            }
        }
    }

    private fun displayGuessedWord(guess: String, byPlayer: Boolean) : GuessItemBinding {
        val guessHolder = GuessItemBinding.inflate(view.layoutInflater)
        val matching = model.getNumberOfMatchingLetters(guess)
        val isAnagram = matching == DataManager.wordLength || model.isAnagram(guess)

        guessHolder.textViewMatchCount.text = when (isAnagram) {
            true -> "A"
            false -> matching.toString()
        }

        if (isAnagram) {
            guessHolder.textViewMatchCount.setOnClickListener {
                Toast.makeText(view.baseContext, "You have guessed an anagram of the secret word", Toast.LENGTH_SHORT).show()
            }
        }

        if (byPlayer && DataManager.assistance) {
            deduceRedTiles(matching)
            deduceGreenTiles(matching)
        }

        if (!byPlayer) {
            guess.forEach { character ->
                val tile = newBlankTile(view)
                tile.text = "$character"
                val key = model.keys[character]!!
                tile.setOnClickListener {
                    showColorPickerDialog(view, key, model)
                }
                key.addListener(tile)
                guessHolder.layoutGuessedWord.addView(tile)
            }
        }

        view.addGuessItem(guessHolder.root)

        return guessHolder
    }

    private fun deduceRedTiles(matchingTiles: Int) {
        var greenTiles = 0
        model.enteredWord.toString().toCharArray()
            .distinct()
            .forEach {
                if (model.keys[it]?.state == KeyState.YES) {
                    greenTiles++
                }
            }
        if (greenTiles == matchingTiles) {
            model.enteredWord
                .map { model.keys[it] }
                .filterNotNull()
                .filter { it.state == KeyState.BLANK }
                .forEach { model.updateState(it, KeyState.NO) }
        }
    }

    private fun deduceGreenTiles(matchingTiles: Int) {
        var nonRedTiles = 0
        model.enteredWord.toString().toCharArray()
            .distinct()
            .forEach {
                if (model.keys[it]?.state != KeyState.NO) {
                    nonRedTiles++
                }
            }
        if (nonRedTiles == matchingTiles) {
            model.enteredWord
                .map { model.keys[it] }
                .filterNotNull()
                .filter { it.state != KeyState.NO }
                .forEach { model.updateState(it, KeyState.YES) }
        }
    }

    fun exitToTitle(didWin: Boolean) {
        model.gameState.didWin = didWin
        model.isGameOver = true
        GameOverDialog(
            activity = view,
            gameState = model.gameState,
            oddsOfLuckyWin = model.validWords.size,
            onCloseAction = {
                view.finish()
            }
        ).show()
    }


    fun backspaceClicked() {
        tapSound()
        vibrate()
        if (model.enteredWord.isNotEmpty()) {
            val ix = model.enteredWord.length - 1
            view.deleteEnteredCharAtIx(ix, model.keys)
            model.enteredWord.deleteCharAt(ix)
        }
    }

    fun backspaceLongClicked() {
        penClickSound()
        for (ix in model.enteredWord.indices) {
            view.deleteEnteredCharAtIx(ix, model.keys)
        }
        model.enteredWord = StringBuilder()
    }

    fun pauseClicked() {
        if (mIsRunning) pause() else play()
    }


    private fun pause(showPauseScreen: Boolean = true) {
        if (mIsRunning) {
            view.setPaused()
            mIsRunning = false
            mHandler.removeCallbacks(mTimer)
            if (!model.isGameOver) {
                if (showPauseScreen) {
                    pauseScreen.launch(null)
                } else {
                    showPauseScreenOnResume = true
                }
            }
        }
    }

    fun onPause() {
        pause(showPauseScreen = false)
    }

    fun onResume() {
        if (showPauseScreenOnResume) {
            pauseScreen.launch(null)
            showPauseScreenOnResume = false
        }
    }

    private val pauseScreen = view.registerForActivityResult(PauseActivity.Contract()) { resultCode ->
        when (resultCode) {
            PauseActivity.RESUME -> play()
            PauseActivity.RESET -> {
                model.keys.forEach { (_, key) -> model.updateState(key, KeyState.BLANK) }
                play()
            }
            PauseActivity.GIVE_UP -> exitToTitle(false)
        }
    }

    fun play() {
        if (!mIsRunning && !model.isGameOver) {
            view.setResumed()
            mIsRunning = true
            mHandler.postDelayed(mTimer, 1000)
        }
    }
}