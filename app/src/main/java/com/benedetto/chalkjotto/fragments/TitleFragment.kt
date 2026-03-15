package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.benedetto.chalkjotto.AboutTag
import com.benedetto.chalkjotto.LearnTag
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.SettingsTag
import com.benedetto.chalkjotto.StatsTag
import com.benedetto.chalkjotto.TutorialTag
import com.benedetto.chalkjotto.databinding.FragmentTitleBinding
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.game.GameActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

private const val MIN_WORD_LENGTH = 4

class TitleFragment : Fragment() {
    private lateinit var binding: FragmentTitleBinding
    private lateinit var startGame: ActivityResultLauncher<Void?>

    override fun onCreate(savedInstanceState: Bundle?) {
        startGame = registerForActivityResult(GameActivity.Contract()) {
            (requireActivity() as MainActivity).goToFragment(it.destination)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTitleBinding.inflate(layoutInflater, container, false)

        val activity = requireActivity() as MainActivity

        // TODO: Add a way to create challenge with a custom word
        binding.buttonNewGame.setOnClickListener {
            DataManager.activeLesson = null
            if (DataManager.hasSeenTutorial) {
                startGame.launch(null)
            } else {
                activity.goToFragment(TutorialTag)
            }
        }
        binding.buttonNewGame.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)

        binding.buttonHelp.setOnClickListener {
            tapSound()
            activity.goToFragment(AboutTag)
        }
        binding.buttonHelp.setOnTouchListener(ScaleOnTouch)

        binding.buttonStats.setOnClickListener {
            tapSound()
            activity.goToFragment(StatsTag)
        }
        binding.buttonStats.setOnTouchListener(ScaleOnTouch)

        binding.buttonLearn.setOnClickListener {
            tapSound()
            activity.goToFragment(LearnTag)
        }
        binding.buttonLearn.setOnTouchListener(ScaleOnTouch)

        binding.seekBarWordDifficulty.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                DataManager.difficulty = progress
                binding.textViewWordDifficulty.text = when (progress) {
                    1 -> activity.getString(R.string.hard)
                    2 -> activity.getString(R.string.insane)
                    else -> activity.getString(R.string.normal)
                }
                updateReadouts()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekBarWordLength.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val len = progress + MIN_WORD_LENGTH
                DataManager.wordLength = len
                binding.textViewWordLength.text = String.format(Locale.getDefault(), "%d", len)
                updateReadouts()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekBarWordDifficulty.progress = DataManager.difficulty
        binding.seekBarWordLength.progress = DataManager.wordLength - MIN_WORD_LENGTH

        binding.buttonSettings.setOnClickListener {
            tapSound()
            activity.goToFragment(SettingsTag)
        }
        binding.buttonSettings.setOnTouchListener(ScaleOnTouch)

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        updateReadouts()
    }

    private fun updateReadouts() {
        val difficulty = DataManager.difficulty
        val wordLength = DataManager.wordLength
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(requireContext()).gameRecordDao()
            val fewest = dao.getFewestGuesses(difficulty, wordLength)
            val fastest = dao.getFastestTime(difficulty, wordLength)
            launch(Dispatchers.Main) {
                binding.textViewFewestGuesses.text = fewest?.toString() ?: "?"
                binding.textViewFastestTime.text =
                    fastest?.let { secondsToTimeDisplay(it) } ?: "?"
            }
        }
    }
}
