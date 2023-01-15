package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.TitleTag
import com.benedetto.chalkjotto.databinding.FragmentLearnBinding
import com.benedetto.chalkjotto.databinding.LessonBinding
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.ScaleOnTouch
import com.benedetto.chalkjotto.definitions.Sound
import com.benedetto.chalkjotto.dialogs.showLessonDialog
import com.benedetto.chalkjotto.dialogs.showTutorialDialog
import com.benedetto.chalkjotto.game.GameState

class LearnFragment : Fragment() {
    private lateinit var binding: FragmentLearnBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLearnBinding.inflate(layoutInflater, container, false)

        val activity = requireActivity() as MainActivity

        binding.rvLessons.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLessons.adapter = LessonAdapter(listOf(
            Lesson(
                "STRONG_START",
                R.string.lesson_strong_start,
                R.string.lesson_strong_start_explanation,
                GameState(
                    greenLetters = HashSet(),
                    yellowLetters = HashSet(),
                    blueLetters = HashSet(),
                    pinkLetters = HashSet(),
                    redLetters = hashSetOf("P", "B", "S", "T", "G", "H", "Y", "L", "M", "O"),
                    guessedWords = listOf("VIXEN","FJORD","GLYPH","TOMBS","QUACK").toMutableList(),
                    targetWord = "RACED",
                    wordDifficulty = 0,
                    wordLength = 5,
                    numSeconds = 0,
                    numGuesses = 0,
                    isGameOver = false,
                    didWin = false,
                    allowNewGuesses = true,
                ).toPayload()
            ),
            Lesson(
                "MAXIMAL_INFO",
                R.string.lesson_maximize_information,
                R.string.lesson_maximize_information_explanation,
                GameState(
                    greenLetters = hashSetOf("M", "E", "S"),
                    yellowLetters = hashSetOf("D", "I", "N", "O"),
                    blueLetters = HashSet(),
                    pinkLetters = HashSet(),
                    redLetters = hashSetOf("G", "R", "A", "F", "T"),
                    guessedWords = listOf("GRAFT", "REAMS", "DINGO", "DRIFT").toMutableList(),
                    targetWord = "DOMES",
                    wordDifficulty = 0,
                    wordLength = 5,
                    numSeconds = 0,
                    numGuesses = 0,
                    isGameOver = false,
                    didWin = false,
                    allowNewGuesses = true,
                ).toPayload()
            )
        ), requireActivity() as MainActivity)

        binding.buttonShowTutorial.setOnClickListener {
            Sound.tapSound()
            showTutorialDialog(activity, false)
        }

        binding.buttonDone.setOnClickListener {
            Sound.tapSound()
            activity.goToFragment(TitleTag)
        }
        binding.buttonDone.setOnTouchListener(ScaleOnTouch)

        return binding.root
    }
}

class LessonAdapter(
    private val lessons: List<Lesson>,
    private val activity: MainActivity
) : RecyclerView.Adapter<LessonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.binding.tvLessonDescription.text = activity.getString(lesson.description)
        if (DataManager.hasCompletedLesson(lesson.id)) {
            holder.binding.icLessonCompleted.setImageResource(R.drawable.ic_baseline_check_box_24)
        } else {
            holder.binding.icLessonCompleted.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
        }
        holder.binding.buttonPlay.setOnClickListener {
            Sound.tapSound()
            showLessonDialog(lesson, activity)
        }
        holder.binding.buttonPlay.setOnTouchListener(ScaleOnTouch)
    }

    override fun getItemCount() = lessons.size
}

class LessonViewHolder(val binding: LessonBinding) : RecyclerView.ViewHolder(binding.root)

data class Lesson(
    val id: String,
    val description: Int,
    val fullText: Int,
    val payload: String
)
