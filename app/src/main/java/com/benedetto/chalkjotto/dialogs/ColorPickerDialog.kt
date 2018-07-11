package com.benedetto.chalkjotto.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.Key
import com.benedetto.chalkjotto.definitions.KeyState
import com.benedetto.chalkjotto.definitions.tapSound
import kotlinx.android.synthetic.main.keystate_menu.view.*


@SuppressLint("InflateParams")
fun showColorPickerDialog(activity: Activity, key: Key) {
	val dialog = Dialog(activity)
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
	val view = (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.keystate_menu, null, false)
	view.keyWhite.setOnClickListener {
		tapSound()
		key.updateState(KeyState.BLANK)
		dialog.dismiss()
	}
	view.keyGreen.setOnClickListener {
		tapSound()
		key.updateState(KeyState.YES)
		dialog.dismiss()
	}
	view.keyRed.setOnClickListener {
		tapSound()
		key.updateState(KeyState.NO)
		dialog.dismiss()
	}
	view.keyYellow.setOnClickListener {
		tapSound()
		key.updateState(KeyState.MAYBE)
		dialog.dismiss()
	}

	dialog.setContentView(view)
	val window = dialog.window
	window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
	window.setBackgroundDrawableResource(android.R.color.transparent)
	window.setGravity(Gravity.CENTER or Gravity.BOTTOM)
	dialog.show()
}