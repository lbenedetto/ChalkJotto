package com.benedetto.chalkjotto.definitions

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

object ScaleOnTouch: View.OnTouchListener {
	private var rect: Rect? = null

	override fun onTouch(v: View?, event: MotionEvent?): Boolean {
		v!!
		event!!
		when (event.action) {
			MotionEvent.ACTION_DOWN -> {
				v.scaleX = .9f
				v.scaleY = .9f
				rect = Rect(v.left, v.top, v.right, v.bottom)
			}
			MotionEvent.ACTION_MOVE -> {
				if (!(rect != null && rect!!.contains(v.left + event.x.toInt(), v.top + event.y.toInt()))) {
					v.scaleX = 1f
					v.scaleY = 1f
				}
			}
			MotionEvent.ACTION_UP -> {
				v.scaleX = 1f
				v.scaleY = 1f
			}
		}
		return false
	}
}