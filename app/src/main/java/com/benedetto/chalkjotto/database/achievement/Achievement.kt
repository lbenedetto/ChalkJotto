package com.benedetto.chalkjotto.database.achievement

import androidx.room.Entity
import com.benedetto.chalkjotto.database.achievement.AchievementId.*
import com.benedetto.chalkjotto.game.GameState

@Entity(tableName = "achievements", primaryKeys = ["id", "difficulty", "length"])
data class Achievement(
    val id: AchievementId,
    val difficulty: Int = -1,
    val length: Int = -1,
    val unlockedAt: Long = System.currentTimeMillis()
) {
    fun displayName(): String = when (id) {
        READ_TUTORIAL -> "Read the tutorial"
        COMPLETE_LESSON_1 -> "Complete Lesson 1"
        COMPLETE_LESSON_2 -> "Complete Lesson 2"
        LUCKY -> "Pure Luck"
        WIN -> "$difficulty $length-Letter"
        FAST -> "$difficulty $length-Letter: Fast"
        EFFICIENT -> "$difficulty $length-Letter: Efficient"
    }

    companion object {
        fun create(id: AchievementId, gameState: GameState): Achievement {
            return Achievement(
                id = id,
                difficulty = gameState.wordDifficulty,
                length = gameState.wordLength
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is Achievement && other.id == id && other.difficulty == difficulty && other.length == length
    }

    override fun hashCode(): Int {
        return id.hashCode() + difficulty.hashCode() + length.hashCode()
    }
}
