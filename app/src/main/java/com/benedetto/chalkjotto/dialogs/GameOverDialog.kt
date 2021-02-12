package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.*
import kotlinx.android.synthetic.main.dialog_game_over.view.*

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
        val popupWindow = PopupDialog(activity, R.layout.dialog_game_over)

        val correctWordLayout = popupWindow.view.layoutCorrectWord
        targetWord.toCharArray().forEach { character ->
            val tile = newBlankTile(activity)
            tile.text = character.toString()
            correctWordLayout.addView(tile)
        }

        // Keeping track of best scores
        if (didWin) {
            val previousFewestGuesses = DataManager.fewestGuesses
            if (previousFewestGuesses == null || previousFewestGuesses > numGuesses) {
                if (previousFewestGuesses != null) {
                    popupWindow.view.textViewInfo2.text = activity.getString(R.string.fewest_guesses_improved, previousFewestGuesses)
                }
                DataManager.fewestGuesses = numGuesses
            }

            val previousFastestTime = DataManager.fastestTimeSeconds
            if (previousFastestTime == null || previousFastestTime > timeUsed) {
                if (previousFastestTime != null) {
                    popupWindow.view.textViewInfo3.text = activity.getString(R.string.fastest_time_improved, secondsToTimeDisplay(previousFastestTime))
                }
                DataManager.fastestTimeSeconds = timeUsed
            }
        }

        popupWindow.view.textViewTitle.text = activity.getString(
                if (didWin && numGuesses == 1L) {
                    R.string.lucky
                } else {
                    if (didWin) R.string.win else R.string.lose
                }
        )
        popupWindow.view.textViewInfo.text =
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

        popupWindow.view.buttonContinue.setOnClickListener {
            tapSound()
            popupWindow.dismiss()
            onCloseAction()
        }

        popupWindow.setOnDismissListener {
            popupWindow.view.buttonContinue.callOnClick()
        }
        popupWindow.show()
    }
}

