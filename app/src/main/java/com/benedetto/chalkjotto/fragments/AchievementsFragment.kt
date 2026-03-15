package com.benedetto.chalkjotto.fragments

import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.database.AppDatabase
import com.benedetto.chalkjotto.database.achievement.Achievement
import com.benedetto.chalkjotto.database.achievement.AchievementId
import com.benedetto.chalkjotto.databinding.FragmentAchievementsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AchievementsFragment : Fragment() {
    private lateinit var binding: FragmentAchievementsBinding

    private val difficulties = listOf(0 to "Normal", 1 to "Hard", 2 to "Insane")
    private val lengths = listOf(4, 5, 6, 7)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAchievementsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val unlocked = AppDatabase.getInstance(requireContext())
                .achievementDao()
                .getAll()
                .toSet()
            launch(Dispatchers.Main) { buildUI(unlocked) }
        }
    }

    private fun buildUI(unlocked: Set<Achievement>) {
        val container = binding.linearLayoutContent

        container.addView(makeTitleView())

        // Measure the label column width from the widest difficulty name, in the
        // actual text size used, so it adapts to the user's font size setting.
        val labelColWidth = measureLabelWidth(difficulties.map { it.second })

        val tiers = listOf(
            AchievementId.WIN to "Win a game",
            AchievementId.EFFICIENT to "Win efficiently",
            AchievementId.FAST to "Win quickly"
        )
        for ((tier, label) in tiers) {
            container.addView(makeSectionHeader(label))
            container.addView(makeGrid(tier, unlocked, labelColWidth))
        }

        container.addView(makeSectionHeader("Special"))
        val specials = listOf(
            Achievement(AchievementId.LUCKY) to "Get Lucky",
            Achievement(AchievementId.READ_TUTORIAL) to "Tutorial",
            Achievement(AchievementId.COMPLETE_LESSON_1) to "Lesson 1",
            Achievement(AchievementId.COMPLETE_LESSON_2) to "Lesson 2"
        )
        container.addView(makeSpecialRow(specials, unlocked))
    }

    /**
     * Grid layout (fills full width):
     *
     *         [  4  ][  5  ][  6  ][  7  ]
     * Normal  [ icon][ icon][ icon][ icon]
     * Hard    [ icon][ icon][ icon][ icon]
     * Insane  [ icon][ icon][ icon][ icon]
     *
     * The label column is sized to fit the longest difficulty name.
     * The four icon columns share the remaining width equally via weight=1.
     * Each icon cell is made square via doOnLayout.
     */
    private fun makeGrid(
        tier: AchievementId,
        unlocked: Set<Achievement>,
        labelColWidth: Int
    ): LinearLayout {
        val cellMargin = dpToPx(2)

        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dpToPx(16) }

            // Header row: blank corner + one label per length
            addView(makeRow(cellMargin) { row ->
                row.addView(makeLabel("", labelColWidth, LinearLayout.LayoutParams.WRAP_CONTENT))
                for (len in lengths) {
                    row.addView(makeLabel(len.toString(), 0, LinearLayout.LayoutParams.WRAP_CONTENT, weight = 1f, marginEnd = cellMargin))
                }
            })

            // One row per difficulty
            for ((diff, diffName) in difficulties) {
                addView(makeRow(cellMargin) { row ->
                    row.addView(makeLabel(diffName, labelColWidth, LinearLayout.LayoutParams.WRAP_CONTENT))
                    for (len in lengths) {
                        val achievement = Achievement(tier, diff, len)
                        row.addView(makeIconCell(unlocked.contains(achievement), cellMargin))
                    }
                })
            }
        }
    }

    private fun makeRow(cellMargin: Int, populate: (LinearLayout) -> Unit): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = cellMargin }
            populate(this)
        }
    }

    private fun makeLabel(
        text: String,
        width: Int,
        height: Int,
        weight: Float = 0f,
        marginEnd: Int = 0
    ): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 13f
            setTextColor(requireContext().getColor(R.color.white))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(width, height, weight).apply {
                this.marginEnd = marginEnd
            }
        }
    }

    private fun makeIconCell(isUnlocked: Boolean, margin: Int): SquareImageView {
        val padding = dpToPx(7)
        return SquareImageView(requireContext()).apply {
            setImageResource(R.drawable.trophy_24px)
            applyChalkBackground()
            setPadding(padding, padding, padding, padding)
            alpha = if (isUnlocked) 1.0f else 0.15f
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = margin
            }
        }
    }

    private inner class SquareImageView(context: android.content.Context) : AppCompatImageView(context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        }
    }

    private fun makeSpecialRow(
        items: List<Pair<Achievement, String>>,
        unlocked: Set<Achievement>
    ): LinearLayout {
        val cellMargin = dpToPx(2)
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dpToPx(16) }

            for ((achievement, label) in items) {
                val isUnlocked = unlocked.contains(achievement)
                addView(LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        marginEnd = cellMargin
                    }

                    val icon = SquareImageView(requireContext()).apply {
                        setImageResource(R.drawable.trophy_24px)
                        applyChalkBackground()
                        val p = dpToPx(7)
                        setPadding(p, p, p, p)
                        alpha = if (isUnlocked) 1.0f else 0.15f
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = dpToPx(4) }
                    }
                    addView(icon)

                    addView(TextView(requireContext()).apply {
                        text = label
                        textSize = 12f
                        setTextColor(requireContext().getColor(R.color.white))
                        gravity = Gravity.CENTER
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    })
                })
            }
        }
    }

    /** Measures the pixel width needed for the widest string in [texts] at 13sp. */
    private fun measureLabelWidth(texts: List<String>): Int {
        val fontScale = resources.configuration.fontScale
        val paint = Paint().apply {
            textSize = 13f * resources.displayMetrics.density * fontScale
        }
        val widest = texts.maxOf { paint.measureText(it) }
        return widest.toInt() + dpToPx(8)  // 8dp padding buffer
    }

    private fun View.applyChalkBackground() {
        val attrs = requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.chalkKeyWhite))
        val bgResId = attrs.getResourceId(0, 0)
        attrs.recycle()
        if (bgResId != 0) setBackgroundResource(bgResId)
    }

    private fun makeTitleView(): TextView {
        return TextView(requireContext()).apply {
            text = getString(R.string.achievements_title)
            textSize = 24f
            setTextColor(requireContext().getColor(R.color.white))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dpToPx(16))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun makeSectionHeader(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 16f
            setTextColor(requireContext().getColor(R.color.white))
            setPadding(0, dpToPx(8), 0, dpToPx(6))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()
}
