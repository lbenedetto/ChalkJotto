package com.benedetto.chalkjotto.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.*
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.game.GameActivity
import kotlinx.android.synthetic.main.fragment_title.*
import kotlinx.android.synthetic.main.fragment_title.seekBarWordDifficulty
import kotlinx.android.synthetic.main.fragment_title.seekBarWordLength
import kotlinx.android.synthetic.main.fragment_title.textViewWordDifficulty
import kotlinx.android.synthetic.main.fragment_title.textViewWordLength

private const val MIN_WORD_LENGTH = 4

class TitleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_title, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mActivity = activity!! as MainActivity

        buttonNewGame.setOnClickListener {
            if (DataManager.hasSeenTutoral)
                mActivity.startActivity(Intent(context, GameActivity::class.java))
            else
                mActivity.goToFragment(TutorialTag)
        }

        buttonNewGame.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)

        seekBarWordDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                DataManager.difficulty = progress
                textViewWordDifficulty.text = when (progress) {
                    1 -> mActivity.getString(R.string.hard)
                    2 -> mActivity.getString(R.string.insane)
                    else -> mActivity.getString(R.string.normal)
                }
                updateReadouts()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarWordLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val len = progress + MIN_WORD_LENGTH
                DataManager.wordLength = len
                textViewWordLength.text = String.format("%d", len)
                updateReadouts()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarWordDifficulty.progress = DataManager.difficulty
        seekBarWordLength.progress = DataManager.wordLength - MIN_WORD_LENGTH
    }

    override fun onResume() {
        super.onResume()
        updateReadouts()
    }

    private fun updateReadouts() {
        textViewFewestGuesses.text = DataManager.fewestGuesses?.toString() ?: "?"
        textViewFastestTime.text = DataManager.fastestTimeSeconds?.let { time -> secondsToTimeDisplay(time) } ?: "?"
    }
}
