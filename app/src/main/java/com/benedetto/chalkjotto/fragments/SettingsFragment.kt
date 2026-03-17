package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.TitleTag
import com.benedetto.chalkjotto.databinding.FragmentSettingsBinding
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.ScaleOnTouch
import com.benedetto.chalkjotto.definitions.Sound.tapSound

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        val activity = requireActivity() as MainActivity

        binding.switchSound.isChecked = DataManager.soundEnabled
        binding.switchSound.setOnClickListener { switch ->
            tapSound()
            DataManager.soundEnabled = (switch as SwitchCompat).isChecked
        }

        binding.switchHighContrast.isChecked = DataManager.highContrastModeEnabled
        binding.switchHighContrast.setOnClickListener { switch ->
            tapSound()
            DataManager.highContrastModeEnabled = (switch as SwitchCompat).isChecked
            activity.themeUpdated(recreate = true)
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

        binding.switchAnalytics.isChecked = DataManager.analyticsEnabled
        binding.switchAnalytics.setOnClickListener { switch ->
            tapSound()
            DataManager.analyticsEnabled = (switch as SwitchCompat).isChecked
        }

        binding.buttonDone.setOnClickListener {
            tapSound()
            activity.goToFragment(TitleTag)
        }
        binding.buttonDone.setOnTouchListener(ScaleOnTouch)

        return binding.root
    }
}
