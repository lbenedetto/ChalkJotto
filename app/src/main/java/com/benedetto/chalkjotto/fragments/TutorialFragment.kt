package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.databinding.FragmentTutorialBinding
import com.benedetto.chalkjotto.database.achievement.AchievementManager
import com.benedetto.chalkjotto.database.AppDatabase
import com.benedetto.chalkjotto.database.achievement.AchievementId
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.game.GameActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TutorialFragment : Fragment() {
    private lateinit var binding: FragmentTutorialBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialBinding.inflate(layoutInflater, container, false)

        val startGame = registerForActivityResult(GameActivity.Contract()) {
            (activity as MainActivity).goToFragment(it.destination)
        }

        if (activity is MainActivity) {
            binding.buttonContinue.setOnClickListener {
                tapSound()
                DataManager.hasSeenTutorial = true
                awardAchievement()
                startGame.launch(null)
            }
        }

        return binding.root
    }

    private fun awardAchievement() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(requireContext()).achievementDao()
            val newlyUnlocked = AchievementManager.awardSimple(AchievementId.READ_TUTORIAL, dao) ?: return@launch

            launch(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Achievement unlocked: ${newlyUnlocked.displayName()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
