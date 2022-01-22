package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.TitleTag
import com.benedetto.chalkjotto.databinding.FragmentAcceptChallengeBinding
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.game.GameActivity
import com.benedetto.chalkjotto.game.GameState


class AcceptChallengeFragment : Fragment() {
    private lateinit var binding: FragmentAcceptChallengeBinding
    private lateinit var startGame: ActivityResultLauncher<Void?>

    override fun onCreate(savedInstanceState: Bundle?) {
        startGame = registerForActivityResult(GameActivity.Contract()) {
            (requireActivity() as MainActivity).goToFragment(it)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAcceptChallengeBinding.inflate(layoutInflater, container, false)

        val activity = requireActivity() as MainActivity

        val payload = activity.intent.data?.getQueryParameter("payload")!!
        val gameState = GameState.fromPayload(payload)

        if (gameState == null) {
            Toast.makeText(
                activity,
                "Failed to load challenge. Try updating your app, or submitting the URL to me in a bug report",
                Toast.LENGTH_LONG
            ).show()
            if (DataManager.isGameInProgress) {
                startGame.launch(null)
            } else {
                activity.goToFragment(TitleTag)
            }
            return binding.root
        }

        binding.textViewWordDifficulty.setText(
            when (gameState.wordDifficulty) {
                2 -> R.string.insane
                1 -> R.string.hard
                else -> R.string.normal
            }
        )

        binding.textViewWordLength.text = "${gameState.wordLength}"

        if (gameState.didWin) {
            binding.textViewGuesses.text = "${gameState.numGuesses}"
            binding.textViewTime.text = secondsToTimeDisplay(gameState.numSeconds)
        } else {
            binding.constraintLayoutSolvedIn.visibility = View.GONE
        }

        val sharingDeductions = gameState.yellowLetters.isNotEmpty()
            || gameState.redLetters.isNotEmpty()
            || gameState.greenLetters.isNotEmpty()

        val sharingGuesses = gameState.guessedWords.size > 0

        if (sharingDeductions && sharingGuesses) {
            binding.tvExplanationShared.setText(R.string.accept_challenge_explanation_shared_guesses_and_deductions)
        } else if (sharingDeductions) {
            binding.tvExplanationShared.setText(R.string.accept_challenge_explanation_shared_deductions)
        } else if (sharingGuesses) {
            binding.tvExplanationShared.setText(R.string.accept_challenge_explanation_shared_guesses)
        } else {
            binding.tvExplanationShared.visibility = View.GONE
        }

        if (gameState.allowNewGuesses) {
            binding.tvExplanationGuessesNotAllowed.visibility = View.GONE
        } else {
            binding.tvExplanationGuessesNotAllowed.setText(R.string.accept_challenge_explanation_guesses_not_allowed)
        }

        if (DataManager.isGameInProgress) {
            binding.tvOverwriteExistingGame.setText(R.string.accept_challenge_explanation_overwrite)
        } else {
            binding.tvOverwriteExistingGame.visibility = View.GONE
        }

        binding.buttonCancel.setOnClickListener { activity.goToFragment(TitleTag) }
        binding.buttonCancel.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)

        binding.buttonAcceptChallenge.setOnClickListener {
            gameState.sanitize()
            if (sharingDeductions || sharingGuesses) {
                gameState.allowSettingRecords = false
            }
            DataManager.gameState = gameState
            startGame.launch(null)
        }
        binding.buttonAcceptChallenge.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)

        return binding.root
    }

    fun toast(str: Int) {
        Toast.makeText(activity, str, Toast.LENGTH_LONG)
            .show()
    }
}
