package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        switchSound.isChecked = DataManager.soundEnabled
        switchSound.setOnClickListener { switch ->
            tapSound()
            DataManager.soundEnabled = (switch as Switch).isChecked
        }

        switchVibrate.isChecked = DataManager.soundEnabled
        switchVibrate.setOnClickListener { switch ->
            tapSound()
            DataManager.vibrationEnabled = (switch as Switch).isChecked
        }
    }
}
