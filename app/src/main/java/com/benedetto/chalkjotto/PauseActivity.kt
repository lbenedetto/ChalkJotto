package com.benedetto.chalkjotto

import android.os.Bundle
import android.view.WindowManager
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound
import com.benedetto.chalkjotto.dialogs.showTutorialDialog
import kotlinx.android.synthetic.main.activity_pause.*


class PauseActivity : AppCompatActivity() {
    companion object {
        const val RESUME = 11
        const val RESET = 12
        const val GIVE_UP = 13
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_pause)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        buttonResume.setOnClickListener {
            tapSound()
            exit(RESUME)
        }

        buttonReset.setOnClickListener {
            tapSound()
            exit(RESET)
        }

        buttonGiveUp.setOnClickListener {
            tapSound()
            exit(GIVE_UP)
        }

        buttonShowTutorial.setOnClickListener {
            tapSound()
            showTutorialDialog(this, false)
        }

        switchSound.isChecked = DataManager.soundEnabled
        switchSound.setOnClickListener { switch ->
            tapSound()
            DataManager.soundEnabled = (switch as Switch).isChecked
        }

        switchVibrate.isChecked = DataManager.vibrationEnabled
        switchVibrate.setOnClickListener { switch ->
            tapSound()
            DataManager.vibrationEnabled = (switch as Switch).isChecked
        }
    }

    fun exit(exitCode: Int) {
        setResult(exitCode)
        finish()
    }

    override fun onBackPressed() {
        exit(RESUME)
    }
}
