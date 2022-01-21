package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.databinding.DialogGameOverBinding
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.game.GameState

class GameOverDialog(
        var activity: Activity,
        var gameState: GameState,
        var oddsOfLuckyWin: Int,
        var onCloseAction: () -> Unit
) {

    fun show() {
        val binding = DialogGameOverBinding.inflate(activity.layoutInflater)
        val popupWindow = PopupDialog(activity, binding.root)

        val inputGuessWordLayout = binding.layoutInputGuessWord
        gameState.targetWord!!.toCharArray().forEach { character ->
            val tile = newBlankTile(activity)
            tile.text = character.toString()
            inputGuessWordLayout.addView(tile)
        }

        // Keeping track of best scores
        if (gameState.didWin) {
            DataManager.wonGames++
            val previousFewestGuesses = DataManager.fewestGuesses
            if (previousFewestGuesses == null || previousFewestGuesses > gameState.numGuesses) {
                if (previousFewestGuesses != null) {
                    binding.textViewInfo2.text = activity.getString(R.string.fewest_guesses_improved, previousFewestGuesses)
                }
                DataManager.fewestGuesses = gameState.numGuesses
            }

            val previousFastestTime = DataManager.fastestTimeSeconds
            if (previousFastestTime == null || previousFastestTime > gameState.numSeconds) {
                if (previousFastestTime != null) {
                    binding.textViewInfo3.text = activity.getString(R.string.fastest_time_improved, secondsToTimeDisplay(previousFastestTime))
                }
                DataManager.fastestTimeSeconds = gameState.numSeconds
            }
        }

        binding.textViewTitle.text = activity.getString(
                if (gameState.didWin && gameState.numGuesses == 1L) {
                    R.string.lucky
                } else {
                    if (gameState.didWin) R.string.win else R.string.lose
                }
        )
        binding.textViewInfo.text =
                if (gameState.didWin) {
                    if (gameState.numGuesses == 1L)
                        activity.getString(R.string.lucky_message, oddsOfLuckyWin)
                    else
                        activity.getString(R.string.info, gameState.numGuesses, secondsToTimeDisplay(gameState.numSeconds))
                } else {
                    if (gameState.numGuesses == 0L)
                        activity.getString(R.string.gave_up_without_trying)
                    else
                        activity.getString(R.string.gave_up, gameState.numGuesses, secondsToTimeDisplay(gameState.numSeconds))
                }

        binding.buttonContinue.setOnClickListener {
            tapSound()
            popupWindow.dismiss()
            onCloseAction()
        }

        popupWindow.setOnDismissListener {
            binding.buttonContinue.callOnClick()
        }
        popupWindow.show()
    }
}

