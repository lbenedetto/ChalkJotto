package com.benedetto.chalkjotto.database.achievement

import com.benedetto.chalkjotto.R

/**
 * @param shortDescriptions [ difficulty ][ length - 4 ], difficulties: 0=Normal, 1=Hard, 2=Insane, lengths: 4–7
 * @param thresholds [ difficulty ][ length - 4 ] threshold value (seconds or guesses), same indexing
 */
enum class AchievementId(
    val isUnique: Boolean = true,
    val shortDescription: Int = 0,
    val shortDescriptions: Array<Array<Int>> = emptyArray(),
    val longDescription: Int = 0,
    val longDescriptions: Array<Int> = emptyArray(),
    val baseDrawable: Int,
    val sectionTitle: Int = 0,
    val thresholds: Array<Array<Int>> = emptyArray(),
    val isHidden: Boolean = false,
) {
    READ_TUTORIAL(
        isUnique = true,
        shortDescription = R.string.achievement_tutorial_short,
        longDescription = R.string.achievement_tutorial_long,
        baseDrawable = R.drawable.achievement_win
    ),
    COMPLETE_LESSON_1(
        isUnique = true,
        shortDescription = R.string.achievement_lesson_1_short,
        longDescription = R.string.achievement_lesson_1_long,
        baseDrawable = R.drawable.achievement_win
    ),
    COMPLETE_LESSON_2(
        isUnique = true,
        shortDescription = R.string.achievement_lesson_2_short,
        longDescription = R.string.achievement_lesson_2_long,
        baseDrawable = R.drawable.achievement_win
    ),
    LUCKY(
        isUnique = true,
        shortDescription = R.string.achievement_lucky_short,
        longDescription = R.string.achievement_lucky_long,
        baseDrawable = R.drawable.achievement_lucky
    ),
    GIVE_UP(
        isUnique = true,
        shortDescription = R.string.achievement_give_up_short,
        longDescription = R.string.achievement_give_up_long,
        baseDrawable = R.drawable.achievement_unknown
    ),
    WIN(
        isUnique = false,
        shortDescriptions = arrayOf(
            arrayOf(
                R.string.achievement_win_normal_4_short,
                R.string.achievement_win_normal_5_short,
                R.string.achievement_win_normal_6_short,
                R.string.achievement_win_normal_7_short
            ),
            arrayOf(
                R.string.achievement_win_hard_4_short,
                R.string.achievement_win_hard_5_short,
                R.string.achievement_win_hard_6_short,
                R.string.achievement_win_hard_7_short
            ),
            arrayOf(
                R.string.achievement_win_insane_4_short,
                R.string.achievement_win_insane_5_short,
                R.string.achievement_win_insane_6_short,
                R.string.achievement_win_insane_7_short
            ),
        ),
        longDescriptions = arrayOf(
            R.string.achievement_win_normal_long,
            R.string.achievement_win_hard_long,
            R.string.achievement_win_insane_long
        ),
        baseDrawable = R.drawable.achievement_win,
        sectionTitle = R.string.achievement_win_title
    ),
    FAST(
        isUnique = false,
        shortDescriptions = arrayOf(
            arrayOf(
                R.string.achievement_fast_normal_4_short,
                R.string.achievement_fast_normal_5_short,
                R.string.achievement_fast_normal_6_short,
                R.string.achievement_fast_normal_7_short
            ),
            arrayOf(
                R.string.achievement_fast_hard_4_short,
                R.string.achievement_fast_hard_5_short,
                R.string.achievement_fast_hard_6_short,
                R.string.achievement_fast_hard_7_short
            ),
            arrayOf(
                R.string.achievement_fast_insane_4_short,
                R.string.achievement_fast_insane_5_short,
                R.string.achievement_fast_insane_6_short,
                R.string.achievement_fast_insane_7_short
            ),
        ),
        longDescriptions = arrayOf(
            R.string.achievement_fast_normal_long,
            R.string.achievement_fast_hard_long,
            R.string.achievement_fast_insane_long
        ),
        baseDrawable = R.drawable.achievement_fast,
        sectionTitle = R.string.achievement_fast_title,
        isHidden = true,
        // seconds to win, keyed by [difficulty][wordLength - 4]
        thresholds = arrayOf(
            arrayOf(15, 30, 60, 90), /* normal */
            arrayOf(20, 40, 80, 120), /* hard */
            arrayOf(25, 50, 100, 150), /* insane */
        ),
    ),
    EFFICIENT(
        isUnique = false,
        shortDescriptions = arrayOf(
            arrayOf(
                R.string.achievement_efficient_normal_4_short,
                R.string.achievement_efficient_normal_5_short,
                R.string.achievement_efficient_normal_6_short,
                R.string.achievement_efficient_normal_7_short
            ),
            arrayOf(
                R.string.achievement_efficient_hard_4_short,
                R.string.achievement_efficient_hard_5_short,
                R.string.achievement_efficient_hard_6_short,
                R.string.achievement_efficient_hard_7_short
            ),
            arrayOf(
                R.string.achievement_efficient_insane_4_short,
                R.string.achievement_efficient_insane_5_short,
                R.string.achievement_efficient_insane_6_short,
                R.string.achievement_efficient_insane_7_short
            ),
        ),
        longDescriptions = arrayOf(
            R.string.achievement_efficient_normal_long,
            R.string.achievement_efficient_hard_long,
            R.string.achievement_efficient_insane_long
        ),
        baseDrawable = R.drawable.achievement_efficient,
        sectionTitle = R.string.achievement_efficient_title,
        isHidden = true,
        // guesses to win, keyed by [difficulty][wordLength - 4]
        thresholds = arrayOf(
            arrayOf(8, 10, 14, 18), /* normal */
            arrayOf(10, 14, 18, 22), /* hard */
            arrayOf(14, 18, 22, 26), /* insane */
        ),
    ),
    ;

    fun threshold(difficulty: Int, wordLength: Int): Long =
        thresholds.getOrNull(difficulty)?.getOrNull(wordLength - 4)?.toLong() ?: 0L
}
