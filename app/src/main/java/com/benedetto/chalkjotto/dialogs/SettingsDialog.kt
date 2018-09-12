package com.benedetto.chalkjotto.dialogs

import android.widget.Switch
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.TitleActivity
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound
import com.google.android.gms.games.Games
import kotlinx.android.synthetic.main.dialog_settings.view.*

//https://developers.google.com/games/services/android/signin#providing_a_sign-in_button
//https://developers.google.com/identity/branding-guidelines
fun showSettingsDialog(activity: TitleActivity) {
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

//	popupWindow.view.buttonSignIn.text = activity.getString(if (activity.gamesManager.isSignedIn()) R.string.sign_out else R.string.sign_in)
//	popupWindow.view.buttonSignIn.setOnClickListener {
//		tapSound()
//		if(activity.gamesManager.isSignedIn()){
//			activity.gamesManager.signOutSilently()
//			popupWindow.view.buttonSignIn.text = activity.getString(R.string.sign_in)
//		}else{
//			activity.gamesManager.signInSilently()
//			popupWindow.view.buttonSignIn.text = activity.getString(R.string.sign_out)
//		}
//	}

	popupWindow.view.buttonClose.setOnClickListener {
		tapSound()
		popupWindow.dismiss()
	}

	popupWindow.show()
}