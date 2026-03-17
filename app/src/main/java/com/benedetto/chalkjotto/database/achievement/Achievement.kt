package com.benedetto.chalkjotto.database.achievement

import android.content.Context
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
    fun shortDescription(context: Context): String = if (id.isUnique) {
        context.getString(id.shortDescription)
    } else {
        context.getString(id.shortDescriptions[difficulty][length - 4])
    }

    fun longDescription(context: Context): String = if (id.isUnique) {
        context.getString(id.longDescription)
    } else {
        context.getString(id.longDescriptions[difficulty], length)
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
