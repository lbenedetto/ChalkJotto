package com.benedetto.chalkjotto.definitions

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.view.View
import android.view.animation.AnimationUtils
import com.benedetto.chalkjotto.R
import java.util.*

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

fun isSdkAtLeastLollipop(): Boolean {
	return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
}

fun secondsToTimeDisplay(numSeconds: Long): String {
	return String.format("%02d:%02d", (numSeconds % 3600) / 60, numSeconds % 60)
}

fun animatePopIn(view: View) {
	val zoomIn = AnimationUtils.loadAnimation(view.context, R.anim.zoom_into_place)
	view.clearAnimation()
	view.startAnimation(zoomIn)
}

fun getKeyHashMap(activity: Activity): HashMap<String, Key> {
	val keys = HashMap<String, Key>()
	keys["A"] = Key("A", activity.findViewById(R.id.keyA))
	keys["B"] = Key("B", activity.findViewById(R.id.keyB))
	keys["C"] = Key("C", activity.findViewById(R.id.keyC))
	keys["D"] = Key("D", activity.findViewById(R.id.keyD))
	keys["E"] = Key("E", activity.findViewById(R.id.keyE))
	keys["F"] = Key("F", activity.findViewById(R.id.keyF))
	keys["G"] = Key("G", activity.findViewById(R.id.keyG))
	keys["H"] = Key("H", activity.findViewById(R.id.keyH))
	keys["I"] = Key("I", activity.findViewById(R.id.keyI))
	keys["J"] = Key("J", activity.findViewById(R.id.keyJ))
	keys["K"] = Key("K", activity.findViewById(R.id.keyK))
	keys["L"] = Key("L", activity.findViewById(R.id.keyL))
	keys["M"] = Key("M", activity.findViewById(R.id.keyM))
	keys["N"] = Key("N", activity.findViewById(R.id.keyN))
	keys["O"] = Key("O", activity.findViewById(R.id.keyO))
	keys["P"] = Key("P", activity.findViewById(R.id.keyP))
	keys["Q"] = Key("Q", activity.findViewById(R.id.keyQ))
	keys["R"] = Key("R", activity.findViewById(R.id.keyR))
	keys["S"] = Key("S", activity.findViewById(R.id.keyS))
	keys["T"] = Key("T", activity.findViewById(R.id.keyT))
	keys["U"] = Key("U", activity.findViewById(R.id.keyU))
	keys["V"] = Key("V", activity.findViewById(R.id.keyV))
	keys["W"] = Key("W", activity.findViewById(R.id.keyW))
	keys["X"] = Key("X", activity.findViewById(R.id.keyX))
	keys["Y"] = Key("Y", activity.findViewById(R.id.keyY))
	keys["Z"] = Key("Z", activity.findViewById(R.id.keyZ))
	return keys
}

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start