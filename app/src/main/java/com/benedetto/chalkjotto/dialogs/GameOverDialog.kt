package com.benedetto.chalkjotto.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import com.benedetto.chalkjotto.GameActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.secondsToTimeDisplay
import com.benedetto.chalkjotto.definitions.tapSound
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import kotlinx.android.synthetic.main.dialog_game_over.view.*

@SuppressLint("InflateParams")
fun showGameOverDialog(activity: GameActivity, didWin: Boolean) {
	activity.isGameOver = true
	activity.pause()
	val popupWindow = PopupDialog(activity, R.layout.dialog_game_over)

	val correctWordLayout = popupWindow.view.layoutCorrectWord
	activity.targetWord.toCharArray().forEach { character ->
		val tile = activity.newBlankTile()
		tile.text = character.toString()
		correctWordLayout.addView(tile)
	}

	popupWindow.view.textViewTitle.text = activity.getString(
			if (didWin && activity.numGuesses == 1) {
				R.string.lucky
			} else {
				if (didWin) R.string.win else R.string.lose
			}
	)
	popupWindow.view.textViewInfo.text =
			if (didWin) {
				if (activity.numGuesses == 1)
					activity.getString(R.string.lucky_message, activity.getOdds())
				else
					activity.getString(R.string.info, activity.numGuesses, secondsToTimeDisplay(activity.numSeconds))
			} else {
				if (activity.numGuesses == 0)
					activity.getString(R.string.gave_up_without_trying)
				else
					activity.getString(R.string.gave_up, activity.numGuesses, secondsToTimeDisplay(activity.numSeconds))
			}

	popupWindow.view.buttonContinue.setOnClickListener {
		tapSound()
		popupWindow.dismiss()
		activity.finish()
	}

	popupWindow.view.buttonReport.setOnClickListener {
		tapSound()
		AlertDialog.Builder(activity)
				.setMessage(String.format("Are you sure you want to report the word \"%s\"?", activity.targetWord))
				.setTitle("Report Word")
				.setIcon(android.R.drawable.ic_dialog_email)
				.setPositiveButton(android.R.string.yes) { _, _ ->
					//TODO: Submit report
				}
				.setNegativeButton(android.R.string.no, null)
				.show()
	}

	popupWindow.setOnDismissListener {
		popupWindow.view.buttonContinue.callOnClick()
	}
	popupWindow.show()

	if (didWin) {
		val account = GoogleSignIn.getLastSignedInAccount(activity)
		if (account != null) {
			val achievementHash = when (activity.wordLength) {
				5 -> when (activity.wordDifficulty) {
					1 -> R.string.achievement_hard_5_letter
					2 -> R.string.achievement_insane_5_letter
					else -> R.string.achievement_normal_5_letter
				}
				6 -> when (activity.wordDifficulty) {
					1 -> R.string.achievement_hard_6_letter
					2 -> R.string.achievement_insane_6_letter
					else -> R.string.achievement_normal_6_letter
				}
				7 -> when (activity.wordDifficulty) {
					1 -> R.string.achievement_hard_7_letter
					2 -> R.string.achievement_insane_7_letter
					else -> R.string.achievement_normal_7_letter
				}
				else -> when (activity.wordDifficulty) {
					1 -> R.string.achievement_hard_4_letter
					2 -> R.string.achievement_insane_4_letter
					else -> R.string.achievement_normal_4_letter
				}
			}
			Games.getGamesClient(activity, account).setViewForPopups(activity.findViewById(android.R.id.content))
			val client = Games.getAchievementsClient(activity, account)
			client.unlock(activity.resources.getString(achievementHash))
			if (didWin && activity.numGuesses == 1) client.unlock(activity.resources.getString(R.string.achievement_lottery_winner))
		}
	}
}

