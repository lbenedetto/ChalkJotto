package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import android.content.Intent
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.AchievementPopup
import com.benedetto.chalkjotto.ShareChallengeTag
import com.benedetto.chalkjotto.TitleTag
import com.benedetto.chalkjotto.databinding.DialogGameOverBinding
import com.benedetto.chalkjotto.database.achievement.AchievementManager
import com.benedetto.chalkjotto.database.analytics.AnalyticsManager
import com.benedetto.chalkjotto.database.AppDatabase
import com.benedetto.chalkjotto.database.gamerecord.GameRecord
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.ScaleOnTouch
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.definitions.newBlankTile
import com.benedetto.chalkjotto.definitions.secondsToTimeDisplay
import com.benedetto.chalkjotto.game.GameActivity.Contract.Companion.DESTINATION
import com.benedetto.chalkjotto.game.GameActivity.Contract.Companion.DID_WIN
import com.benedetto.chalkjotto.game.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameOverDialog(
    var activity: Activity,
    var gameState: GameState,
    var oddsOfLuckyWin: Int,
    var scope: CoroutineScope,
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

        // Persist game record and update record-breaking UI
        scope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(activity).gameRecordDao()
            if (gameState.didWin && gameState.allowSettingRecords) {
                val previousFewestGuesses = dao.getFewestGuesses(gameState.wordDifficulty, gameState.wordLength)
                val previousFastestTime = dao.getFastestTime(gameState.wordDifficulty, gameState.wordLength)

                if (previousFewestGuesses != null && previousFewestGuesses > gameState.numGuesses) {
                    activity.runOnUiThread {
                        binding.textViewInfo2.text = activity.getString(
                            R.string.fewest_guesses_improved,
                            previousFewestGuesses
                        )
                    }
                }
                if (previousFastestTime != null && previousFastestTime > gameState.numSeconds) {
                    activity.runOnUiThread {
                        binding.textViewInfo3.text = activity.getString(
                            R.string.fastest_time_improved,
                            secondsToTimeDisplay(previousFastestTime)
                        )
                    }
                }
            }
            if (gameState.allowSettingRecords && gameState.numGuesses > 0) {
                val record = GameRecord(
                    timestamp = System.currentTimeMillis(),
                    difficulty = gameState.wordDifficulty,
                    wordLength = gameState.wordLength,
                    numGuesses = gameState.numGuesses,
                    numSeconds = gameState.numSeconds,
                    didWin = gameState.didWin
                )
                dao.insert(record)
                if (!DataManager.analyticsConsentShown) {
                    activity.runOnUiThread { showConsentPanel(binding, record) }
                } else {
                    AnalyticsManager.upload(record)
                }
            }
            val db = AppDatabase.getInstance(activity)
            val newlyUnlocked = AchievementManager.checkAndUnlock(gameState, db.achievementDao())
            if (newlyUnlocked.isNotEmpty()) {
                AchievementPopup.enqueue(newlyUnlocked)
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
                    activity.getString(
                        R.string.info,
                        gameState.numGuesses,
                        secondsToTimeDisplay(gameState.numSeconds)
                    )
            } else {
                if (gameState.numGuesses == 0L)
                    activity.getString(R.string.gave_up_without_trying)
                else
                    activity.getString(
                        R.string.gave_up,
                        gameState.numGuesses,
                        secondsToTimeDisplay(gameState.numSeconds)
                    )
            }

        var clicked = false
        binding.buttonContinue.setOnTouchListener(ScaleOnTouch)
        binding.buttonContinue.setOnClickListener {
            clicked = true
            tapSound()
            popupWindow.dismiss()
            val intent = Intent()
            intent.putExtra(DESTINATION, TitleTag)
            intent.putExtra(DID_WIN, gameState.didWin)
            activity.setResult(Activity.RESULT_OK, intent)
            onCloseAction()
        }

        binding.buttonChallengeAFriend.setOnTouchListener(ScaleOnTouch)
        binding.buttonChallengeAFriend.setOnClickListener {
            clicked = true
            tapSound()
            popupWindow.dismiss()
            val intent = Intent()
            intent.putExtra(DESTINATION, ShareChallengeTag)
            intent.putExtra(DID_WIN, gameState.didWin)
            activity.setResult(Activity.RESULT_OK, intent)
            onCloseAction()
        }

        popupWindow.setOnDismissListener {
            if (!clicked) {
                binding.buttonContinue.callOnClick()
            }
        }
        popupWindow.show()
    }

    private fun showConsentPanel(binding: DialogGameOverBinding, record: GameRecord) {
        binding.gameOverCard.visibility = android.view.View.GONE
        binding.analyticsConsentCard.visibility = android.view.View.VISIBLE
        binding.buttonConsentAccept.setOnTouchListener(ScaleOnTouch)
        binding.buttonConsentAccept.setOnClickListener {
            tapSound()
            DataManager.analyticsEnabled = true
            DataManager.analyticsConsentShown = true
            AnalyticsManager.upload(record)
            binding.analyticsConsentCard.visibility = android.view.View.GONE
            binding.gameOverCard.visibility = android.view.View.VISIBLE
        }
        binding.buttonConsentDecline.setOnTouchListener(ScaleOnTouch)
        binding.buttonConsentDecline.setOnClickListener {
            tapSound()
            DataManager.analyticsConsentShown = true
            binding.analyticsConsentCard.visibility = android.view.View.GONE
            binding.gameOverCard.visibility = android.view.View.VISIBLE
        }
    }
}

