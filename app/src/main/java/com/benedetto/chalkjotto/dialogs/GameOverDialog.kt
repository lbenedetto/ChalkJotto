package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.databinding.DialogGameOverBinding
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.definitions.Sound.tapSound

class GameOverDialog(
        var activity: Activity,
        var didWin: Boolean,
        var targetWord: String,
        var numGuesses: Long,
        var oddsOfLuckyWin: Int,
        var timeUsed: Long,
        var onCloseAction: () -> Unit
) {

    fun show() {
        val binding = DialogGameOverBinding.inflate(activity.layoutInflater)
        val popupWindow = PopupDialog(activity, binding.root)

        val correctWordLayout = binding.layoutCorrectWord
        targetWord.toCharArray().forEach { character ->
            val tile = newBlankTile(activity)
            tile.text = character.toString()
            correctWordLayout.addView(tile)
        }

        // Keeping track of best scores
        if (didWin) {
            DataManager.wonGames++
            val previousFewestGuesses = DataManager.fewestGuesses
            if (previousFewestGuesses == null || previousFewestGuesses > numGuesses) {
                if (previousFewestGuesses != null) {
                    binding.textViewInfo2.text = activity.getString(R.string.fewest_guesses_improved, previousFewestGuesses)
                }
                DataManager.fewestGuesses = numGuesses
            }

            val previousFastestTime = DataManager.fastestTimeSeconds
            if (previousFastestTime == null || previousFastestTime > timeUsed) {
                if (previousFastestTime != null) {
                    binding.textViewInfo3.text = activity.getString(R.string.fastest_time_improved, secondsToTimeDisplay(previousFastestTime))
                }
                DataManager.fastestTimeSeconds = timeUsed
            }
        }

        binding.textViewTitle.text = activity.getString(
                if (didWin && numGuesses == 1L) {
                    R.string.lucky
                } else {
                    if (didWin) R.string.win else R.string.lose
                }
        )
        binding.textViewInfo.text =
                if (didWin) {
                    if (numGuesses == 1L)
                        activity.getString(R.string.lucky_message, oddsOfLuckyWin)
                    else
                        activity.getString(R.string.info, numGuesses, secondsToTimeDisplay(timeUsed))
                } else {
                    if (numGuesses == 0L)
                        activity.getString(R.string.gave_up_without_trying)
                    else
                        activity.getString(R.string.gave_up, numGuesses, secondsToTimeDisplay(timeUsed))
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

