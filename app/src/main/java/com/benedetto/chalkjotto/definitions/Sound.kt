package com.benedetto.chalkjotto.definitions

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.benedetto.chalkjotto.R

object Sound {
    private var penClick: MediaPlayer? = null
    private var penClickUp: MediaPlayer? = null
    private var penClickDown: MediaPlayer? = null
    private var tap: MediaPlayer? = null
    private var Vibrator: Vibrator? = null
    private var initialized = false

    fun init(context: Context) {
        if (initialized) {
            return
        }

        penClick = MediaPlayer.create(context, R.raw.pen_click)
        penClickUp = MediaPlayer.create(context, R.raw.pen_click_up)
        penClickDown = MediaPlayer.create(context, R.raw.pen_click_down)
        tap = MediaPlayer.create(context, R.raw.tap)
        tap!!.setVolume(.5f, .5f)
        Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        initialized = true
    }

    fun penClickSound() {
        playSound(penClick)
    }

    fun penClickDownSound() {
        playSound(penClickDown)
    }

    fun penClickUpSound() {
        playSound(penClickUp)
    }

    fun tapSound() {
        playSound(tap)
    }

    private fun playSound(sound: MediaPlayer?) {
        if (shouldPlaySound()) {
            sound?.start()
        }
    }

    private fun shouldPlaySound(): Boolean {
        return DataManager.soundEnabled && initialized
    }

    fun vibrate() {
        if (!DataManager.vibrationEnabled || !initialized) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            Vibrator?.vibrate(25)
        }

    }
}