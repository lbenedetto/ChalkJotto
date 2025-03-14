package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.databinding.FragmentTutorialBinding
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.game.GameActivity

class TutorialFragment : Fragment() {
    private lateinit var binding: FragmentTutorialBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTutorialBinding.inflate(layoutInflater, container, false)

        val startGame = registerForActivityResult(GameActivity.Contract()) {
            (activity as MainActivity).goToFragment(it.destination)
        }

        if (activity is MainActivity) {
            binding.buttonContinue.setOnClickListener {
                tapSound()
                DataManager.hasSeenTutorial = true
                startGame.launch(null)
            }
        }

        return binding.root
    }
}
