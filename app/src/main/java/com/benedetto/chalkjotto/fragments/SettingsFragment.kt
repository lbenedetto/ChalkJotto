package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.definitions.getThemeFont

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activity = requireActivity() as MainActivity
        val fontFamily = FontFamily(getThemeFont(requireContext()))

        var soundEnabled by mutableStateOf(DataManager.soundEnabled)
        var highContrastEnabled by mutableStateOf(DataManager.highContrastModeEnabled)
        var vibrationEnabled by mutableStateOf(DataManager.vibrationEnabled)
        var assistanceEnabled by mutableStateOf(DataManager.assistance)
        var analyticsEnabled by mutableStateOf(DataManager.analyticsEnabled)

        val composeView = ComposeView(requireContext())
        composeView.setContent {
            SettingsScreen(
                fontFamily = fontFamily,
                soundEnabled = soundEnabled,
                highContrastEnabled = highContrastEnabled,
                vibrationEnabled = vibrationEnabled,
                assistanceEnabled = assistanceEnabled,
                analyticsEnabled = analyticsEnabled,
                onSoundChanged = { checked ->
                    tapSound()
                    soundEnabled = checked
                    DataManager.soundEnabled = checked
                },
                onHighContrastChanged = { checked ->
                    tapSound()
                    highContrastEnabled = checked
                    DataManager.highContrastModeEnabled = checked
                    activity.themeUpdated(recreate = true)
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
                },
                onAnalyticsChanged = { checked ->
                    tapSound()
                    analyticsEnabled = checked
                    DataManager.analyticsEnabled = checked
                }
            )
        }
        return composeView
    }
}

@Composable
private fun SettingsScreen(
    fontFamily: FontFamily,
    soundEnabled: Boolean,
    highContrastEnabled: Boolean,
    vibrationEnabled: Boolean,
    assistanceEnabled: Boolean,
    analyticsEnabled: Boolean,
    onSoundChanged: (Boolean) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit,
    onVibrationChanged: (Boolean) -> Unit,
    onAssistanceChanged: (Boolean) -> Unit,
    onAnalyticsChanged: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingRow(
                label = stringResource(R.string.sound),
                checked = soundEnabled,
                onCheckedChange = onSoundChanged,
                fontFamily = fontFamily
            )
            SettingRow(
                label = stringResource(R.string.high_contrast),
                checked = highContrastEnabled,
                onCheckedChange = onHighContrastChanged,
                fontFamily = fontFamily
            )
            SettingRow(
                label = stringResource(R.string.vibration),
                checked = vibrationEnabled,
                onCheckedChange = onVibrationChanged,
                fontFamily = fontFamily
            )
            SettingRow(
                label = stringResource(R.string.assistance),
                checked = assistanceEnabled,
                onCheckedChange = onAssistanceChanged,
                fontFamily = fontFamily,
                description = stringResource(R.string.assistance_description)
            )
            SettingRow(
                label = stringResource(R.string.analytics_share_data),
                checked = analyticsEnabled,
                onCheckedChange = onAnalyticsChanged,
                fontFamily = fontFamily,
                description = stringResource(R.string.analytics_share_data_description)
            )
        }
    }
}

@Composable
fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    fontFamily: FontFamily,
    description: String? = null
) {
    val white = colorResource(R.color.white)
    val architectsDaughter = FontFamily(Font(R.font.architects_daughter))
    var descriptionExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (description != null) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { descriptionExpanded = !descriptionExpanded }
                else Modifier
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontFamily = fontFamily,
                fontSize = 16.sp,
                color = white,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = white,
                    checkedTrackColor = white.copy(alpha = 0.4f),
                    uncheckedThumbColor = white.copy(alpha = 0.6f),
                    uncheckedTrackColor = white.copy(alpha = 0.2f),
                    uncheckedBorderColor = white.copy(alpha = 0.3f)
                )
            )
        }
        if (description != null) {
            val fadeMask = if (!descriptionExpanded) Modifier
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Black,
                                androidx.compose.ui.graphics.Color.Transparent
                            ),
                            startY = size.height * 0.4f,
                            endY = size.height
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
            else Modifier
            Text(
                text = description,
                fontFamily = architectsDaughter,
                fontSize = 12.sp,
                color = white.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .then(if (descriptionExpanded) Modifier else Modifier.heightIn(max = 60.dp))
                    .padding(bottom = 4.dp)
                    .then(fadeMask)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        fontFamily = FontFamily.Default,
        soundEnabled = true,
        highContrastEnabled = false,
        vibrationEnabled = true,
        assistanceEnabled = true,
        analyticsEnabled = false,
        onSoundChanged = {},
        onHighContrastChanged = {},
        onVibrationChanged = {},
        onAssistanceChanged = {},
        onAnalyticsChanged = {},
    )
}
