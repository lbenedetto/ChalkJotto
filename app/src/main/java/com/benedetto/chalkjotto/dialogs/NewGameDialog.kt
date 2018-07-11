package com.benedetto.chalkjotto.dialogs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import com.benedetto.chalkjotto.GameActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.TitleActivity
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound
import kotlinx.android.synthetic.main.dialog_new_game.view.*

private const val MIN_WORD_LENGTH = 4
@SuppressLint("InflateParams")
fun showNewGameDialog(activity: TitleActivity) {
	val popupWindow = PopupDialog(activity, R.layout.dialog_new_game)

	popupWindow.view.buttonContinue.setOnClickListener {
		tapSound()
		val intent = Intent(activity, GameActivity::class.java)
		val bundle = Bundle()
		bundle.putInt("difficulty", DataManager.difficulty)
		bundle.putInt("length", DataManager.wordLength)
		intent.putExtras(bundle)
		activity.startActivity(intent)
		popupWindow.popup.dismiss()
	}
	popupWindow.view.seekBarWordDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
		override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
			DataManager.difficulty = progress
			popupWindow.view.textViewWordDifficulty.text = when (progress) {
				1 -> activity.getString(R.string.hard)
				2 -> activity.getString(R.string.insane)
				else -> activity.getString(R.string.normal)
			}
		}

		override fun onStopTrackingTouch(seekBar: SeekBar?) {}
		override fun onStartTrackingTouch(seekBar: SeekBar?) {}
	})
	popupWindow.view.seekBarWordLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
		override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
			val len = progress + MIN_WORD_LENGTH
			DataManager.wordLength = len
			popupWindow.view.textViewWordLength.text = String.format("%d", len)
		}

		override fun onStopTrackingTouch(seekBar: SeekBar?) {}
		override fun onStartTrackingTouch(seekBar: SeekBar?) {}
	})

	popupWindow.view.seekBarWordDifficulty.progress = DataManager.difficulty
	popupWindow.view.seekBarWordLength.progress = DataManager.wordLength - MIN_WORD_LENGTH
	popupWindow.show()
}