package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import androidx.core.content.ContextCompat.getColor
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound
import kotlinx.android.synthetic.main.fragment_tutorial.view.*

fun showTutorialDialog(activity: Activity, blockBackground: Boolean) {
    val popupWindow = PopupDialog(activity, R.layout.dialog_tutorial)

    popupWindow.view.buttonContinue.setOnClickListener {
        tapSound()
        DataManager.hasSeenTutoral = true
        popupWindow.popup.dismiss()
    }
    if (!blockBackground) popupWindow.view.setBackgroundColor(getColor(activity, android.R.color.transparent))

    popupWindow.setOnDismissListener {
        popupWindow.view.buttonContinue.callOnClick()
    }
    popupWindow.show()
}