package com.benedetto.chalkjotto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.benedetto.chalkjotto.databinding.ActivityPauseBinding
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.dialogs.showTutorialDialog
import com.benedetto.chalkjotto.fragments.SettingRow


class PauseActivity : JottoActivity() {
    companion object {
        const val RESUME = 11
        const val RESET = 12
        const val GIVE_UP = 13
    }

    lateinit var binding: ActivityPauseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPauseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.buttonResume.setOnTouchListener(ScaleOnTouch)
        binding.buttonResume.setOnClickListener {
            tapSound()
            exit(RESUME)
        }

        binding.buttonReset.setOnTouchListener(ScaleOnTouch)
        binding.buttonReset.setOnClickListener {
            tapSound()
            exit(RESET)
        }

        binding.buttonGiveUp.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
        binding.buttonGiveUp.setOnClickListener {
            exit(GIVE_UP)
        }

        binding.buttonShowTutorial.setOnTouchListener(ScaleOnTouch)
        binding.buttonShowTutorial.setOnClickListener {
            tapSound()
            showTutorialDialog(this, false)
        }

        var soundEnabled by mutableStateOf(DataManager.soundEnabled)
        var highContrastEnabled by mutableStateOf(DataManager.highContrastModeEnabled)
        var vibrationEnabled by mutableStateOf(DataManager.vibrationEnabled)
        var assistanceEnabled by mutableStateOf(DataManager.assistance)

        binding.composeToggles.setContent {
            PauseToggles(
                soundEnabled = soundEnabled,
                highContrastEnabled = highContrastEnabled,
                vibrationEnabled = vibrationEnabled,
                assistanceEnabled = assistanceEnabled,
                onSoundChanged = { checked ->
                    soundEnabled = checked
                    DataManager.soundEnabled = checked
                    tapSound()
                },
                onHighContrastChanged = { checked ->
                    tapSound()
                    highContrastEnabled = checked
                    DataManager.highContrastModeEnabled = checked
                    themeUpdated(recreate = true)
                },
                onVibrationChanged = { checked ->
                    tapSound()
                    vibrationEnabled = checked
                    DataManager.vibrationEnabled = checked
                },
                onAssistanceChanged = { checked ->
                    tapSound()
                    assistanceEnabled = checked
                    DataManager.assistance = checked
                }
            )
        }
        binding.root.fitToWindowInsets()
        onBackPressedDispatcher.addCallback(this) {
            exit(RESUME)
        }
    }

    private fun exit(exitCode: Int) {
        setResult(exitCode)
        finish()
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

@Composable
fun PauseToggles(
    soundEnabled: Boolean,
    highContrastEnabled: Boolean,
    vibrationEnabled: Boolean,
    assistanceEnabled: Boolean,
    onSoundChanged: (Boolean) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit,
    onVibrationChanged: (Boolean) -> Unit,
    onAssistanceChanged: (Boolean) -> Unit,
) {
    val fontFamily = FontFamily(getThemeFont(LocalContext.current))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingRow(stringResource(R.string.sound), soundEnabled, onSoundChanged, fontFamily)
        SettingRow(stringResource(R.string.high_contrast), highContrastEnabled, onHighContrastChanged, fontFamily)
        SettingRow(stringResource(R.string.vibration), vibrationEnabled, onVibrationChanged, fontFamily)
        SettingRow(stringResource(R.string.assistance), assistanceEnabled, onAssistanceChanged, fontFamily)
    }
}