package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.benedetto.chalkjotto.definitions.dpToPx

class PopupDialog(private val activity: Activity, view: View) {
    var popup = PopupWindow(
            view,
            activity.window.decorView.width - dpToPx(32),
            LinearLayout.LayoutParams.WRAP_CONTENT
    )

    fun setOnDismissListener(listener: () -> Unit) {
        popup.setOnDismissListener(listener)
    }

    fun show() {
        popup.elevation = dpToPx(10).toFloat()
        popup.isOutsideTouchable = true
        popup.showAtLocation(activity.findViewById(android.R.id.content), Gravity.CENTER, 0, 0)
    }

    fun dismiss() {
        popup.dismiss()
    }

}