package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import android.widget.Switch
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound
import kotlinx.android.synthetic.main.dialog_settings.view.*
//https://developers.google.com/games/services/android/signin#providing_a_sign-in_button
//https://developers.google.com/identity/branding-guidelines
fun showSettingsDialog(activity: Activity) {
	val popupWindow = PopupDialog(activity, R.layout.dialog_settings)

	popupWindow.view.switchSound.isChecked = DataManager.soundEnabled
	popupWindow.view.switchSound.setOnClickListener { switch ->
		tapSound()
		DataManager.soundEnabled = (switch as Switch).isChecked
	}

	popupWindow.view.switchVibrate.isChecked = DataManager.soundEnabled
	popupWindow.view.switchVibrate.setOnClickListener { switch ->
		tapSound()
		DataManager.vibrationEnabled = (switch as Switch).isChecked
	}

	popupWindow.view.buttonShowTutorial.setOnClickListener {
		tapSound()
		showTutorialDialog(activity, false)
	}

	//TODO: Implement google games services
	popupWindow.view.buttonSignIn.setOnClickListener {
		tapSound()
	}

	popupWindow.view.buttonClose.setOnClickListener {
		tapSound()
		popupWindow.dismiss()
	}

	popupWindow.show()
}