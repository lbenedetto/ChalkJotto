package com.benedetto.chalkjotto.definitions

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.view.View
import android.view.animation.AnimationUtils
import com.benedetto.chalkjotto.R
import java.util.*
import android.content.res.TypedArray


operator fun View.OnTouchListener.plus(other: View.OnTouchListener): View.OnTouchListener {
    return ConfigurableOnTouchListener().addBehavior(this).addBehavior(other)
}

fun penClickSound() {
    if (DataManager.soundEnabled) {
        Sound.PenClick.start()
    }
}

fun penClickDownSound() {
    if (DataManager.soundEnabled) {
        Sound.PenClickDown.start()
    }
}

fun penClickUpSound() {
    if (DataManager.soundEnabled) {
        Sound.PenClickUp.start()
    }
}

fun tapSound() {
    if (DataManager.soundEnabled) {
        Sound.Tap.start()
    }
}

fun getStatusBarHeight(resources: Resources): Int {
    var statusBarHeight = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
}

fun navigationBarHeight(resources: Resources): Int {
    var navigationBarHeight = 0
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        navigationBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return navigationBarHeight
}

fun vibrate() {
    if (DataManager.vibrationEnabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Sound.Vibrate.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            Sound.Vibrate.vibrate(25)
        }
    }
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun secondsToTimeDisplay(numSeconds: Long): String {
    return String.format("%02d:%02d", (numSeconds % 3600) / 60, numSeconds % 60)
}

fun animatePopIn(view: View) {
    val zoomIn = AnimationUtils.loadAnimation(view.context, R.anim.zoom_into_place)
    view.clearAnimation()
    view.startAnimation(zoomIn)
}

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start