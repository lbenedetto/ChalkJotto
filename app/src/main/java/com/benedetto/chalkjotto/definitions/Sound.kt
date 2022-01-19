package com.benedetto.chalkjotto.definitions

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import com.benedetto.chalkjotto.R

object Sound {
    lateinit var PenClick: MediaPlayer
    lateinit var PenClickUp: MediaPlayer
    lateinit var PenClickDown: MediaPlayer
    lateinit var Tap: MediaPlayer
    lateinit var Vibrator: Vibrator

    fun init(context: Context) {
        PenClick = MediaPlayer.create(context, R.raw.pen_click)
        PenClickUp = MediaPlayer.create(context, R.raw.pen_click_up)
        PenClickDown = MediaPlayer.create(context, R.raw.pen_click_down)
        Tap = MediaPlayer.create(context, R.raw.tap)
        Tap.setVolume(.5f, .5f)
        Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager  = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator;
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        }

    }
}