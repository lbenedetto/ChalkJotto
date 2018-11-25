package com.benedetto.chalkjotto.definitions

import com.benedetto.chalkjotto.R

enum class GameIds(val next: DifficultyIds) {
    NORMAL(NormalIds),
    HARD(HardIds),
    INSANE(InsaneIds);

    companion object {
        fun of(difficulty: Int): DifficultyIds {
            return when (difficulty) {
                0 -> NORMAL.next
                1 -> HARD.next
                else -> INSANE.next
            }
        }
    }
}

abstract class DifficultyIds(private val FOUR: IdsTriple, private val FIVE: IdsTriple, private val SIX: IdsTriple, private val SEVEN: IdsTriple) {
    fun of(length: Int): IdsTriple {
        return when (length) {
            4 -> FOUR
            5 -> FIVE
            6 -> SIX
            else -> SEVEN
        }
    }
}

object NormalIds : DifficultyIds(
        IdsTriple(R.string.achievement_normal_4_letter, R.string.leaderboard_normal_4_letter_fastest_time, R.string.leaderboard_normal_4_letter_fewest_guesses),
        IdsTriple(R.string.achievement_normal_5_letter, R.string.leaderboard_normal_5_letter_fastest_time, R.string.leaderboard_normal_5_letter_fewest_guesses),
        IdsTriple(R.string.achievement_normal_6_letter, R.string.leaderboard_normal_6_letter_fastest_time, R.string.leaderboard_normal_6_letter_fewest_guesses),
        IdsTriple(R.string.achievement_normal_7_letter, R.string.leaderboard_normal_7_letter_fastest_time, R.string.leaderboard_normal_7_letter_fewest_guesses)
)

object HardIds : DifficultyIds(
        IdsTriple(R.string.achievement_hard_4_letter, R.string.leaderboard_hard_4_letter_fastest_time, R.string.leaderboard_hard_4_letter_fewest_guesses),
        IdsTriple(R.string.achievement_hard_5_letter, R.string.leaderboard_hard_5_letter_fastest_time, R.string.leaderboard_hard_5_letter_fewest_guesses),
        IdsTriple(R.string.achievement_hard_6_letter, R.string.leaderboard_hard_6_letter_fastest_time, R.string.leaderboard_hard_6_letter_fewest_guesses),
        IdsTriple(R.string.achievement_hard_7_letter, R.string.leaderboard_hard_7_letter_fastest_time, R.string.leaderboard_hard_7_letter_fewest_guesses)
)

object InsaneIds : DifficultyIds(
        IdsTriple(R.string.achievement_insane_4_letter, R.string.leaderboard_insane_4_letter_fastest_time, R.string.leaderboard_insane_4_letter_fewest_guesses),
        IdsTriple(R.string.achievement_insane_5_letter, R.string.leaderboard_insane_5_letter_fastest_time, R.string.leaderboard_insane_5_letter_fewest_guesses),
        IdsTriple(R.string.achievement_insane_6_letter, R.string.leaderboard_insane_6_letter_fastest_time, R.string.leaderboard_insane_6_letter_fewest_guesses),
        IdsTriple(R.string.achievement_insane_7_letter, R.string.leaderboard_insane_7_letter_fastest_time, R.string.leaderboard_insane_7_letter_fewest_guesses)
)

class IdsTriple(val achievement: Int, val timeLeaderboard: Int, val guessesLeaderboard: Int)





