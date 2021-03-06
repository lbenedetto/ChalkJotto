package com.benedetto.chalkjotto.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.benedetto.chalkjotto.PauseActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.dialogs.GameOverDialog
import com.benedetto.chalkjotto.dialogs.showColorPickerDialog
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.guess_item.view.*

@SuppressLint("InflateParams", "ClickableViewAccessibility")
class GamePresenter(private val model: GameModel, val view: GameActivity) {
    private var mTimer: Runnable
    private var mHandler = Handler()
    private var mIsRunning = false
    private var showPauseScreenOnResume = false

    init {
        mTimer = object : Runnable {
            override fun run() {
                model.numSeconds++
                view.setTimer(model.numSeconds)
                if (mIsRunning) mHandler.postDelayed(this, 1000)
            }
        }
        view.refillUserInputFieldWithTiles()

        model.keys.forEach { (_, key) ->
            key.view.setOnTouchListener(ScaleOnTouch)
            key.view.setOnClickListener {
                tapSound()
                vibrate()
                if (model.enteredWord.length < DataManager.wordLength) {
                    val letter = view.layoutCorrectWord.getChildAt(model.enteredWord.length) as TextView
                    letter.setOnClickListener {
                        showColorPickerDialog(view, key)
                    }
                    letter.text = key.letter
                    letter.setBackgroundResource(key.state.background)
                    model.enteredWord.append(letter.text.toString())
                    key.addListener(letter)
                }
            }

            key.view.setOnLongClickListener {
                showColorPickerDialog(view, key)
                true
            }
        }
    }

    fun submitButtonPressed() {
        if (model.enteredWord.length == DataManager.wordLength) {
            if (model.validWords.contains(model.enteredWord.toString().toUpperCase())) {
                model.numGuesses++
                if (model.enteredWord.toString() == model.targetWord) {
                    view.refillUserInputFieldWithTiles()
                    model.isGameOver = true
                    pause()
                    exitToTitle(didWin = true)
                } else {
                    val guess = model.enteredWord.toString()
                    val guessHolder = view.layoutInflater.inflate(R.layout.guess_item, null)
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
                    if (DataManager.assistance) {
                        deduceRedTiles(matching)
                        deduceGreenTiles(matching)
                    }
                    view.moveWordToView(guessHolder.layoutGuessedWord)
                    view.addGuessItem(guessHolder)
                    view.refillUserInputFieldWithTiles()
                    view.setNumberOfGuesses(model.numGuesses)
                    model.clearEnteredWord()
                }
            } else {
                view.layoutCorrectWord.clearAnimation()
                view.layoutCorrectWord.startAnimation(AnimationUtils.loadAnimation(view, R.anim.shake))
            }
        }
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
                    .forEach {
                        if (model.keys[it]?.state == KeyState.BLANK)
                            model.keys[it]?.updateState(KeyState.NO)
                    }
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
                    .forEach {
                        if (model.keys[it]?.state != KeyState.NO)
                            model.keys[it]?.updateState(KeyState.YES)
                    }
        }
    }

    fun exitToTitle(didWin: Boolean) {
        GameOverDialog(
                activity = view,
                didWin = didWin,
                targetWord = model.targetWord,
                numGuesses = model.numGuesses,
                oddsOfLuckyWin = model.validWords.size,
                timeUsed = model.numSeconds,
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
            if (!model.isGameOver && showPauseScreen) {
                if (showPauseScreen) {
                    showPauseScreen()
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
            showPauseScreen()
            showPauseScreenOnResume = false
        }
    }

    private fun showPauseScreen() {
        view.startActivityForResult(Intent(view, PauseActivity::class.java), 1)
    }

    fun play() {
        if (!mIsRunning && !model.isGameOver) {
            view.setResumed()
            mIsRunning = true
            mHandler.postDelayed(mTimer, 1000)
        }
    }
}