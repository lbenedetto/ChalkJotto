package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import androidx.core.content.ContextCompat.getColor
import com.benedetto.chalkjotto.databinding.DialogTutorialBinding
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound

fun showTutorialDialog(activity: Activity, blockBackground: Boolean) {
    val binding = DialogTutorialBinding.inflate(activity.layoutInflater)
    val popupWindow = PopupDialog(activity, binding.root)

    binding.buttonContinue.setOnClickListener {
        tapSound()
        DataManager.hasSeenTutoral = true
        popupWindow.popup.dismiss()
    }
    if (!blockBackground) binding.root.setBackgroundColor(getColor(activity, android.R.color.transparent))

    popupWindow.setOnDismissListener {
        binding.buttonContinue.callOnClick()
    }
    popupWindow.show()
}