package com.benedetto.chalkjotto.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.TitleTag
import com.benedetto.chalkjotto.databinding.FragmentShareChallengeBinding
import com.benedetto.chalkjotto.definitions.*


class ShareChallengeFragment : Fragment() {
    private lateinit var binding: FragmentShareChallengeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShareChallengeBinding.inflate(layoutInflater, container, false)

        val activity = requireActivity() as MainActivity

        val gameState = DataManager.gameState!!

        DataManager.gameState!!.targetWord!!.forEach { letter ->
            val tile = newBlankTile(activity)
            tile.text = "$letter"
            binding.layoutInputGuessWord.addView(tile)
        }

        if (gameState.didWin) {
            binding.tvExplanation.setText(R.string.share_challenge_explanation_won)
            binding.textViewGuesses.text = "${gameState.numGuesses}"
            binding.textViewTime.text = secondsToTimeDisplay(gameState.numSeconds)
        } else {
            binding.tvExplanation.setText(R.string.share_challenge_explanation_lost)
            binding.textViewGuesses.visibility = View.GONE
            binding.textViewGuessesLabel.visibility = View.GONE
            binding.textViewTime.visibility = View.GONE
            binding.textViewTimeLabel.visibility = View.GONE
            binding.switchAllowNewGuesses.visibility = View.GONE
            binding.tvAllowNewGuessesToggleLabel.visibility = View.GONE
        }

        binding.textViewWordDifficulty.setText(
            when (gameState.wordDifficulty) {
                2 -> R.string.insane
                1 -> R.string.hard
                else -> R.string.normal
            }
        )

        binding.textViewWordLength.text = "${gameState.wordLength}"

        binding.tvShareGuessesToggleLabel.setOnClickListener {
            toast(R.string.share_your_guesses_explanation)
        }

        binding.tvShareDeductionsToggleLabel.setOnClickListener {
            toast(R.string.share_your_deductions_explanation)
        }

        binding.tvAllowNewGuessesToggleLabel.setOnClickListener {
            toast(R.string.allow_new_guesses_explanation)
        }

        binding.switchShareGuesses.setOnClickListener {
            Sound.tapSound()
            val switch = it as SwitchCompat
            if (!switch.isChecked
                && !binding.switchAllowNewGuesses.isChecked
            ) {
                binding.switchAllowNewGuesses.isChecked = true
            }
        }

        binding.switchShareDeductions.setOnClickListener {
            Sound.tapSound()
            val switch = it as SwitchCompat
            if (!switch.isChecked
                && !binding.switchShareGuesses.isChecked
                && !binding.switchAllowNewGuesses.isChecked
            ) {
                binding.switchAllowNewGuesses.isChecked = true
            }
        }

        binding.switchAllowNewGuesses.setOnClickListener {
            Sound.tapSound()
            val switch = it as SwitchCompat
            // If we don't allow new guesses, we must share existing guesses
            if (!switch.isChecked
                && !binding.switchShareGuesses.isChecked
            ) {
                binding.switchShareGuesses.isChecked = true
            }
        }

        binding.buttonShareChallenge.setOnClickListener {
            val shareState = gameState.createShareInstance(
                binding.switchShareGuesses.isChecked,
                binding.switchShareDeductions.isChecked,
                binding.switchAllowNewGuesses.isChecked
            )
            // TODO: create challenge page.
            val payload = shareState.toPayload()
            val url = "https://larsbenedetto.work/ChalkJotto/challenge.html?payload=$payload"

            val share = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Can you deduce the secret word?")
                putExtra(Intent.EXTRA_TEXT, url)
            }
            startActivity(Intent.createChooser(share, "Challenge a friend"))
        }
        binding.buttonShareChallenge.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)

        binding.buttonDone.setOnClickListener {
            Sound.tapSound()
            activity.goToFragment(TitleTag)
        }
        binding.buttonDone.setOnTouchListener(ScaleOnTouch)

        return binding.root
    }

    fun toast(str: Int) {
        Toast.makeText(activity, str, Toast.LENGTH_LONG)
            .show()
    }
}
