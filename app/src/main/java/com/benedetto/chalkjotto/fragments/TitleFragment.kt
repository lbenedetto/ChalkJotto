package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.*
import com.benedetto.chalkjotto.databinding.FragmentTitleBinding
import com.benedetto.chalkjotto.definitions.*

private const val MIN_WORD_LENGTH = 4

class TitleFragment : Fragment() {
    private lateinit var binding: FragmentTitleBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTitleBinding.inflate(layoutInflater, container, false)

        val activity = requireActivity() as MainActivity

        binding.buttonNewGame.setOnClickListener { startGame(activity) }

        binding.buttonNewGame.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)

        binding.seekBarWordDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

        binding.seekBarWordLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val len = progress + MIN_WORD_LENGTH
                DataManager.wordLength = len
                binding.textViewWordLength.text = String.format("%d", len)
                updateReadouts()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekBarWordDifficulty.progress = DataManager.difficulty
        binding.seekBarWordLength.progress = DataManager.wordLength - MIN_WORD_LENGTH

        return binding.root
    }



    override fun onResume() {
        super.onResume()
        updateReadouts()
    }

    private fun updateReadouts() {
       binding.textViewFewestGuesses.text = DataManager.fewestGuesses?.toString() ?: "?"
       binding.textViewFastestTime.text = DataManager.fastestTimeSeconds?.let { time -> secondsToTimeDisplay(time) } ?: "?"
    }
}
