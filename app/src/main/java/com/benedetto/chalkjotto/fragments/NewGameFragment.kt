package com.benedetto.chalkjotto.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound
import kotlinx.android.synthetic.main.fragment_new_game.*

private const val MIN_WORD_LENGTH = 4

class NewGameFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mActivity = activity!! as MainActivity

        buttonContinue.setOnClickListener {
            //TODO: Fancy transition animation
            tapSound()
            mActivity.goToGame(DataManager.difficulty, DataManager.wordLength)
        }

        seekBarWordDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                DataManager.difficulty = progress
                textViewWordDifficulty.text = when (progress) {
                    1 -> mActivity.getString(R.string.hard)
                    2 -> mActivity.getString(R.string.insane)
                    else -> mActivity.getString(R.string.normal)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarWordLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val len = progress + MIN_WORD_LENGTH
                DataManager.wordLength = len
                textViewWordLength.text = String.format("%d", len)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarWordDifficulty.progress = DataManager.difficulty
        seekBarWordLength.progress = DataManager.wordLength - MIN_WORD_LENGTH
    }
}
