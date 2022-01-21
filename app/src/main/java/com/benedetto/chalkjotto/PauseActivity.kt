package com.benedetto.chalkjotto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.benedetto.chalkjotto.databinding.ActivityPauseBinding
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.dialogs.showTutorialDialog


class PauseActivity : JottoActivity() {
    companion object {
        const val RESUME = 11
        const val RESET = 12
        const val GIVE_UP = 13
    }

    lateinit var binding: ActivityPauseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        binding = ActivityPauseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.buttonResume.setOnClickListener {
            tapSound()
            exit(RESUME)
        }

        binding.buttonReset.setOnClickListener {
            tapSound()
            exit(RESET)
        }

        binding.buttonGiveUp.setOnClickListener {
            tapSound()
            exit(GIVE_UP)
        }

        binding.buttonShowTutorial.setOnClickListener {
            tapSound()
            showTutorialDialog(this, false)
        }

        binding.switchSound.isChecked = DataManager.soundEnabled
        binding.switchSound.setOnClickListener { switch ->
            tapSound()
            DataManager.soundEnabled = (switch as SwitchCompat).isChecked
        }

        binding.switchVibrate.isChecked = DataManager.vibrationEnabled
        binding.switchVibrate.setOnClickListener { switch ->
            tapSound()
            DataManager.vibrationEnabled = (switch as SwitchCompat).isChecked
        }

        binding.switchAssistance.isChecked = DataManager.assistance
        binding.switchAssistance.setOnClickListener { switch ->
            tapSound()
            DataManager.assistance = (switch as SwitchCompat).isChecked
        }
    }

    fun exit(exitCode: Int) {
        setResult(exitCode)
        finish()
    }

    override fun onBackPressed() {
        exit(RESUME)
    }

    class Contract : ActivityResultContract<Void?, Int>() {
        override fun createIntent(context: Context, input: Void?): Intent {
            return Intent(context, PauseActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int {
            return resultCode
        }
    }
}
