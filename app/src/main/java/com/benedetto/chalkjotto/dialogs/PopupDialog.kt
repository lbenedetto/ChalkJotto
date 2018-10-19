package com.benedetto.chalkjotto.dialogs

import android.app.Activity
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.benedetto.chalkjotto.definitions.dpToPx
import com.benedetto.chalkjotto.definitions.isSdkAtLeastLollipop

class PopupDialog(private val activity: Activity, layout: Int) {
	var view = activity.layoutInflater.inflate(layout, null)!!
	var popup = PopupWindow(
			view,
			activity.window.decorView.width - dpToPx(64),
			LinearLayout.LayoutParams.WRAP_CONTENT
	)

	fun setOnDismissListener(listener: () -> Unit) {
		popup.setOnDismissListener(listener)
	}

	fun show() {
		if (isSdkAtLeastLollipop()) popup.elevation = dpToPx(10).toFloat()
		popup.isOutsideTouchable = true
		popup.showAtLocation(activity.findViewById(android.R.id.content), Gravity.CENTER, 0, 0)
	}

	fun dismiss() {
		popup.dismiss()
	}

}