package com.benedetto.chalkjotto.database.achievement

import com.benedetto.chalkjotto.R

enum class AchievementId(
    val isUnique: Boolean = true,
    val shortDescription: Int,
    val baseDrawable: Int,
) {
    READ_TUTORIAL(true, R.string.achievement_tutorial, R.drawable.achievement_win),
    COMPLETE_LESSON_1(true, R.string.achievement_lesson_1, R.drawable.achievement_win),
    COMPLETE_LESSON_2(true, R.string.achievement_lesson_2, R.drawable.achievement_win),
    LUCKY(true, R.string.achievement_lucky, R.drawable.achievement_lucky),
    WIN(false, R.string.achievement_win, R.drawable.achievement_win),
    FAST(false, R.string.achievement_fast, R.drawable.achievement_fast),
    EFFICIENT(false, R.string.achievement_efficient, R.drawable.achievement_efficient),
    ;
}
