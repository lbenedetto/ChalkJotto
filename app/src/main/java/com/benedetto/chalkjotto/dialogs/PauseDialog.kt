package com.benedetto.chalkjotto.dialogs

import android.annotation.SuppressLint
import android.widget.Switch
import com.benedetto.chalkjotto.GameActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Key
import com.benedetto.chalkjotto.definitions.KeyState
import com.benedetto.chalkjotto.definitions.tapSound
import kotlinx.android.synthetic.main.dialog_pause.view.*

@SuppressLint("InflateParams")
fun showPauseDialog(activity: GameActivity, keys: HashMap<String, Key>) {
	val popupWindow = PopupDialog(activity, R.layout.dialog_pause)

	popupWindow.view.buttonResume.setOnClickListener {
		tapSound()
		popupWindow.dismiss()
		activity.play()
	}

	popupWindow.view.buttonReset.setOnClickListener {
		tapSound()
		keys.forEach { _, key -> key.updateState(KeyState.BLANK) }
	}

	popupWindow.view.buttonGiveUp.setOnClickListener {
		tapSound()
		popupWindow.dismiss()
		showGameOverDialog(activity, false)
	}

	popupWindow.view.buttonShowTutorial.setOnClickListener {
		tapSound()
		showTutorialDialog(activity, false)
	}

	popupWindow.view.switchSound.isChecked = DataManager.soundEnabled
	popupWindow.view.switchSound.setOnClickListener { switch ->
		tapSound()
		DataManager.soundEnabled = (switch as Switch).isChecked
	}

	popupWindow.view.switchVibrate.isChecked = DataManager.vibrationEnabled
	popupWindow.view.switchVibrate.setOnClickListener { switch ->
		tapSound()
		DataManager.vibrationEnabled = (switch as Switch).isChecked
	}

	popupWindow.setOnDismissListener {
		popupWindow.view.buttonResume.callOnClick()
	}
	popupWindow.show()
}