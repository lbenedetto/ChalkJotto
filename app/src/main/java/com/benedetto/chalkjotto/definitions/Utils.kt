package com.benedetto.chalkjotto.definitions

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.benedetto.chalkjotto.R
import java.util.*


operator fun View.OnTouchListener.plus(other: View.OnTouchListener): View.OnTouchListener {
    return ConfigurableOnTouchListener().addBehavior(this).addBehavior(other)
}

fun vibrate() {
    if (DataManager.vibrationEnabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Sound.Vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            Sound.Vibrator.vibrate(25)
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

fun newBlankTile(context: Context): TextView {
    val tile = TextView(context)
    tile.setTextColor(ContextCompat.getColor(context, android.R.color.white))
    tile.typeface = ResourcesCompat.getFont(context, R.font.architects_daughter)
    tile.textSize = 34f
    tile.setBackgroundResource(KeyState.BLANK.background)
    val size = dpToPx(40)
    tile.gravity = Gravity.CENTER
    val params = ConstraintLayout.LayoutParams(size, size)
    params.topMargin = 2
    params.bottomMargin = 2
    params.leftMargin = 2
    params.rightMargin = 2
    tile.layoutParams = params
    tile.elevation = dpToPx(2).toFloat()

    return tile
}

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start