package com.benedetto.chalkjotto.database.achievement

import com.benedetto.chalkjotto.R

enum class AchievementId(
    val isUnique: Boolean = true,
    val shortDescription: Int
) {
    READ_TUTORIAL(true, R.string.achievement_tutorial),
    COMPLETE_LESSON_1(true, R.string.achievement_lesson_1),
    COMPLETE_LESSON_2(true, R.string.achievement_lesson_2),
    LUCKY(true, R.string.achievement_lucky),
    WIN(false, R.string.achievement_win),
    FAST(false, R.string.achievement_fast),
    EFFICIENT(false, R.string.achievement_efficient),
    ;
}
