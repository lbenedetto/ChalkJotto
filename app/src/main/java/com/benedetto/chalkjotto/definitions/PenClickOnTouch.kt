package com.benedetto.chalkjotto.definitions

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import com.benedetto.chalkjotto.definitions.Sound.penClickDownSound
import com.benedetto.chalkjotto.definitions.Sound.penClickUpSound

object PenClickOnTouch : View.OnTouchListener {
    private var rect: Rect? = null
    private var isOutSide = false

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v!!
        event!!
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isOutSide = false
                penClickDownSound()
                rect = Rect(v.left, v.top, v.right, v.bottom)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isOutSide) {
                    if (!(rect != null && rect!!.contains(v.left + event.x.toInt(), v.top + event.y.toInt()))) {
                        isOutSide = true
                        penClickUpSound()
                    } else {
                        isOutSide = false
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isOutSide) penClickUpSound()
            }
        }
        return false
    }
}