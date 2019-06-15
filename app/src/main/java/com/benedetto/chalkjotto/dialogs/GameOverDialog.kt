package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.newBlankTile
import com.benedetto.chalkjotto.definitions.secondsToTimeDisplay
import com.benedetto.chalkjotto.definitions.tapSound
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

