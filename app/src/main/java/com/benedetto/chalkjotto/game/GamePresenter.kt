package com.benedetto.chalkjotto.game

import android.annotation.SuppressLint
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.dialogs.showColorPickerDialog
import com.benedetto.chalkjotto.dialogs.showGameOverDialog
import com.benedetto.chalkjotto.dialogs.showPauseDialog
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.guess_item.view.*

@SuppressLint("InflateParams", "ClickableViewAccessibility")
class GamePresenter(private val model: GameModel, val view: GameFragment) {
    private var mTimer: Runnable
    private var mHandler = Handler()
    private var mIsRunning = false

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
                    letter.setOnLongClickListener {
                        showColorPickerDialog(view.context!!, key)
                        true
                    }
                    letter.text = key.letter
                    letter.setBackgroundResource(key.state.background)
                    model.enteredWord.append(letter.text.toString())
                    key.addListener(letter)
                }
            }

            key.view.setOnLongClickListener {
                showColorPickerDialog(view.context!!, key)
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
//					showGameOverDialog(view, true)
                } else {
                    val guessHolder = view.layoutInflater.inflate(R.layout.guess_item, null)
                    guessHolder.textViewMatchCount.text = model.getNumberOfMatchingLetters(model.enteredWord.toString()).toString()
                    view.moveWordToView(guessHolder.layoutGuessedWord)
                    view.addGuessItem(guessHolder)
                    view.refillUserInputFieldWithTiles()
                    view.setNumberOfGuesses(model.numGuesses)
                    model.clearEnteredWord()
                }
            } else {
                view.layoutCorrectWord.clearAnimation()
                view.layoutCorrectWord.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.shake))
            }
        }
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
        for (ix in 0 until model.enteredWord.length) {
            view.deleteEnteredCharAtIx(ix, model.keys)
        }
        model.enteredWord = StringBuilder()
    }

    fun pauseClicked() {
        if (mIsRunning) pause() else play()
    }


    @SuppressLint("InflateParams")
    fun pause() {
        if (mIsRunning) {
            view.setPaused()
            mIsRunning = false
            mHandler.removeCallbacks(mTimer)
//			if (!model.isGameOver) showPauseDialog(view.activity, model.keys)
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