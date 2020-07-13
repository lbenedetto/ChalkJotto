package com.benedetto.chalkjotto.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.benedetto.chalkjotto.PauseActivity.Companion.GIVE_UP
import com.benedetto.chalkjotto.PauseActivity.Companion.RESET
import com.benedetto.chalkjotto.PauseActivity.Companion.RESUME
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.*
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.view.*
import java.util.*


class GameActivity : AppCompatActivity() {
    private lateinit var gamePresenter: GamePresenter
    private lateinit var gameModel: GameModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_game)
        DataManager.init(this)
        val wordDifficulty = DataManager.difficulty
        val wordLength = DataManager.wordLength

        gameModel = GameModel(getKeyHashMap(layoutKeyboard!!), resources, wordDifficulty, wordLength)
        gamePresenter = GamePresenter(gameModel, this)

        buttonPause.setOnClickListener { gamePresenter.pauseClicked() }
        keySubmit.setOnClickListener { gamePresenter.submitButtonPressed() }
        keyBackspace.setOnClickListener { gamePresenter.backspaceClicked() }
        keyBackspace.setOnLongClickListener {
            gamePresenter.backspaceLongClicked()
            true
        }

        keyBackspace.setOnTouchListener(ScaleOnTouch)
        keySubmit.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
        buttonPause.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)

        gamePresenter.play()
    }

    override fun onBackPressed() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESUME -> gamePresenter.play()
            RESET -> {
                gameModel.keys.forEach { (_, key) -> key.updateState(KeyState.BLANK) }
                gamePresenter.play()
            }
            GIVE_UP -> gamePresenter.exitToTitle(false)
        }
    }

    fun refillUserInputFieldWithTiles() {
        layoutCorrectWord.removeAllViews()
        for (i in 0 until DataManager.wordLength) {
            val tile = newBlankTile(this)
            layoutCorrectWord.addView(tile)
            animatePopIn(tile)
        }
    }

    fun moveWordToView(to: LinearLayout) {
        for (i in layoutCorrectWord.childCount - 1 downTo 0) {
            val letterView = layoutCorrectWord.getChildAt(i) as TextView?
            layoutCorrectWord.removeView(letterView)
            to.addView(letterView, 0)
            animatePopIn(letterView as View)
        }
    }

    fun deleteEnteredCharAtIx(ix: Int, keys: HashMap<Char, Key>) {
        val tile = layoutCorrectWord.getChildAt(ix) as TextView
        keys[tile.text[0]]!!.removeListener(tile)
        tile.text = ""
        tile.setBackgroundResource(KeyState.BLANK.background)
        tile.setOnLongClickListener(null)
    }

    fun addGuessItem(view: View) {
        layoutGuessedWords.addView(view)
        svGuessedWords.post {
            svGuessedWords.fullScroll(View.FOCUS_DOWN)
        }
    }

    fun setNumberOfGuesses(guesses: Long) {
        textViewGuessCount.text = guesses.toString()
    }

    fun setPaused() {
        buttonPause.setImageResource(android.R.drawable.ic_media_play)
    }

    fun setResumed() {
        buttonPause.setImageResource(android.R.drawable.ic_media_pause)
    }

    fun setTimer(numSeconds: Long) {
        textViewTimer.text = secondsToTimeDisplay(numSeconds)
    }

    override fun onPause() {
        super.onPause()
        gamePresenter.pause()
    }

    private fun getKeyHashMap(view: View): HashMap<Char, Key> {
        val keys = HashMap<Char, Key>()

        keys['A'] = Key("A", view.keyA)
        keys['B'] = Key("B", view.keyB)
        keys['C'] = Key("C", view.keyC)
        keys['D'] = Key("D", view.keyD)
        keys['E'] = Key("E", view.keyE)
        keys['F'] = Key("F", view.keyF)
        keys['G'] = Key("G", view.keyG)
        keys['H'] = Key("H", view.keyH)
        keys['I'] = Key("I", view.keyI)
        keys['J'] = Key("J", view.keyJ)
        keys['K'] = Key("K", view.keyK)
        keys['L'] = Key("L", view.keyL)
        keys['M'] = Key("M", view.keyM)
        keys['N'] = Key("N", view.keyN)
        keys['O'] = Key("O", view.keyO)
        keys['P'] = Key("P", view.keyP)
        keys['Q'] = Key("Q", view.keyQ)
        keys['R'] = Key("R", view.keyR)
        keys['S'] = Key("S", view.keyS)
        keys['T'] = Key("T", view.keyT)
        keys['U'] = Key("U", view.keyU)
        keys['V'] = Key("V", view.keyV)
        keys['W'] = Key("W", view.keyW)
        keys['X'] = Key("X", view.keyX)
        keys['Y'] = Key("Y", view.keyY)
        keys['Z'] = Key("Z", view.keyZ)
        return keys
    }

}
