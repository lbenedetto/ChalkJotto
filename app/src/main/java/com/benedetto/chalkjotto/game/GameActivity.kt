package com.benedetto.chalkjotto.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.benedetto.chalkjotto.JottoActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.TitleTag
import com.benedetto.chalkjotto.databinding.ActivityGameBinding
import com.benedetto.chalkjotto.definitions.*
import java.util.*


class GameActivity : JottoActivity() {
    private lateinit var gamePresenter: GamePresenter
    private lateinit var gameModel: GameModel
    lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val wordDifficulty = DataManager.difficulty
        val wordLength = DataManager.wordLength

        var gameState = DataManager.gameState
        if (gameState == null || gameState.isGameOver) {
            gameState = GameState.newGame(wordDifficulty, wordLength)
        }
        gameModel = GameModel(getKeyHashMap(), resources, gameState)
        gamePresenter = GamePresenter(gameModel, this)

        binding.buttonPause.setOnClickListener { gamePresenter.pauseClicked() }
        binding.keySubmit.setOnClickListener { gamePresenter.submitButtonPressed() }
        binding.keyBackspace.setOnClickListener { gamePresenter.backspaceClicked() }
        binding.keyBackspace.setOnLongClickListener {
            gamePresenter.backspaceLongClicked()
            true
        }

        binding.keyBackspace.setOnTouchListener(ScaleOnTouch)
        binding.keySubmit.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
        binding.buttonPause.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.layoutTableLabels.updatePadding(top = insets.top)
            view.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        gamePresenter.play()

        onBackPressedDispatcher.addCallback(this) {}
    }

    fun refillUserInputFieldWithTiles() {
        binding.layoutInputGuessWord.removeAllViews()
        for (i in 0 until gameModel.gameState.wordLength) {
            val tile = newBlankTile(this)
            binding.layoutInputGuessWord.addView(tile)
            animatePopIn(tile)
        }
    }

    fun moveWordToView(to: LinearLayout) {
        for (i in binding.layoutInputGuessWord.childCount - 1 downTo 0) {
            val letterView = binding.layoutInputGuessWord.getChildAt(i) as TextView?
            binding.layoutInputGuessWord.removeView(letterView)
            to.addView(letterView, 0)
            animatePopIn(letterView as View)
        }
    }

    fun deleteEnteredCharAtIx(ix: Int, keys: HashMap<Char, Key>) {
        val tile = binding.layoutInputGuessWord.getChildAt(ix) as TextView
        keys[tile.text[0]]!!.removeListener(tile)
        tile.text = ""
        tile.setBackgroundResource(KeyState.BLANK.getBackgroundResource(this))
        tile.setOnLongClickListener(null)
    }

    fun addGuessItem(view: View) {
        binding.layoutGuessedWords.addView(view)
        binding.svGuessedWords.post {
            binding.svGuessedWords.fullScroll(View.FOCUS_DOWN)
        }
    }

    fun setNumberOfGuesses(guesses: Long) {
        binding.textViewGuessCount.text = guesses.toString()
    }

    fun setPaused() {
        binding.buttonPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    fun setResumed() {
        binding.buttonPause.setImageResource(R.drawable.ic_baseline_pause_24)
    }

    fun setTimer(numSeconds: Long) {
        binding.textViewTimer.text = secondsToTimeDisplay(numSeconds)
    }

    override fun onPause() {
        super.onPause()
        gamePresenter.onPause()
        DataManager.gameState = gameModel.gameState
    }

    override fun onStart() {
        super.onStart()
        gamePresenter.onResume()
    }

    private fun getKeyHashMap(): HashMap<Char, Key> {
        val keys = HashMap<Char, Key>()
        keys['A'] = Key("A", binding.keyA)
        keys['B'] = Key("B", binding.keyB)
        keys['C'] = Key("C", binding.keyC)
        keys['D'] = Key("D", binding.keyD)
        keys['E'] = Key("E", binding.keyE)
        keys['F'] = Key("F", binding.keyF)
        keys['G'] = Key("G", binding.keyG)
        keys['H'] = Key("H", binding.keyH)
        keys['I'] = Key("I", binding.keyI)
        keys['J'] = Key("J", binding.keyJ)
        keys['K'] = Key("K", binding.keyK)
        keys['L'] = Key("L", binding.keyL)
        keys['M'] = Key("M", binding.keyM)
        keys['N'] = Key("N", binding.keyN)
        keys['O'] = Key("O", binding.keyO)
        keys['P'] = Key("P", binding.keyP)
        keys['Q'] = Key("Q", binding.keyQ)
        keys['R'] = Key("R", binding.keyR)
        keys['S'] = Key("S", binding.keyS)
        keys['T'] = Key("T", binding.keyT)
        keys['U'] = Key("U", binding.keyU)
        keys['V'] = Key("V", binding.keyV)
        keys['W'] = Key("W", binding.keyW)
        keys['X'] = Key("X", binding.keyX)
        keys['Y'] = Key("Y", binding.keyY)
        keys['Z'] = Key("Z", binding.keyZ)
        return keys
    }

    class Contract : ActivityResultContract<Void?, GameResult>() {
        companion object {
            const val DESTINATION = "destination"
            const val DID_WIN = "didWin"
        }
        override fun createIntent(context: Context, input: Void?): Intent {
            return Intent(context, GameActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): GameResult {
            return GameResult(
                intent?.getStringExtra(DESTINATION) ?: TitleTag,
                intent?.getBooleanExtra(DID_WIN, false) ?: false
            )
        }
    }

    data class GameResult(
        val destination: String,
        val didWin: Boolean
    )
}
