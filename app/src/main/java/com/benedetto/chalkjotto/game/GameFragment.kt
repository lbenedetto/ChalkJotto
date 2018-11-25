package com.benedetto.chalkjotto.game

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.*
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_game.view.*
import java.util.*


class GameFragment : Fragment() {
    private lateinit var gamePresenter: GamePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return GameView(inflater.context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val wordDifficulty = arguments!!.getInt("difficulty")
        val wordLength = arguments!!.getInt("length")

        val gameModel = GameModel(getKeyHashMap(view!!), resources, wordDifficulty, wordLength)
        val gamePresenter = GamePresenter(gameModel, this)

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

    private fun newBlankTile(): TextView {
        val tile = TextView(context!!)
        tile.setTextColor(ContextCompat.getColor(context!!, android.R.color.white))
        tile.typeface = ResourcesCompat.getFont(context!!, R.font.architects_daughter)
        tile.textSize = 34f
        tile.setBackgroundResource(KeyState.BLANK.background)
        val size = dpToPx(40)
        tile.gravity = Gravity.CENTER
        val params = ConstraintLayout.LayoutParams(size, size)
        params.setMargins(2, 2, 2, 2)
        tile.layoutParams = params
        tile.elevation = dpToPx(2).toFloat()

        return tile
    }

    fun refillUserInputFieldWithTiles() {
        layoutCorrectWord.removeAllViews()
        for (i in 0 until DataManager.wordLength) {
            val tile = newBlankTile()
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

    fun deleteEnteredCharAtIx(ix: Int, keys: HashMap<String, Key>) {
        val tile = layoutCorrectWord.getChildAt(ix) as TextView
        keys[tile.text]!!.removeListener(tile)
        tile.text = ""
        tile.setBackgroundResource(KeyState.BLANK.background)
        tile.setOnLongClickListener(null)
    }

    fun addGuessItem(view: View) {
        view.layoutGuessedWords.addView(view, 0)
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

    private fun getKeyHashMap(view: View): HashMap<String, Key> {
        val keys = HashMap<String, Key>()

        keys["A"] = Key("A", view.keyA)
        keys["B"] = Key("B", view.keyB)
        keys["C"] = Key("C", view.keyC)
        keys["D"] = Key("D", view.keyD)
        keys["E"] = Key("E", view.keyE)
        keys["F"] = Key("F", view.keyF)
        keys["G"] = Key("G", view.keyG)
        keys["H"] = Key("H", view.keyH)
        keys["I"] = Key("I", view.keyI)
        keys["J"] = Key("J", view.keyJ)
        keys["K"] = Key("K", view.keyK)
        keys["L"] = Key("L", view.keyL)
        keys["M"] = Key("M", view.keyM)
        keys["N"] = Key("N", view.keyN)
        keys["O"] = Key("O", view.keyO)
        keys["P"] = Key("P", view.keyP)
        keys["Q"] = Key("Q", view.keyQ)
        keys["R"] = Key("R", view.keyR)
        keys["S"] = Key("S", view.keyS)
        keys["T"] = Key("T", view.keyT)
        keys["U"] = Key("U", view.keyU)
        keys["V"] = Key("V", view.keyV)
        keys["W"] = Key("W", view.keyW)
        keys["X"] = Key("X", view.keyX)
        keys["Y"] = Key("Y", view.keyY)
        keys["Z"] = Key("Z", view.keyZ)
        return keys
    }

    fun setInsets(navBarSize: Int, statusBarSize: Int) {
        (clRootInset.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = navBarSize
        (buttonPause.layoutParams as ViewGroup.MarginLayoutParams).topMargin = statusBarSize
    }

}
