package com.benedetto.chalkjotto.database.achievement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun save(achievement: Achievement)

    suspend fun isUnlocked(achievement: Achievement) : Boolean {
        return findById(achievement.id, achievement.difficulty, achievement.length) != null
    }

    suspend fun unlock(achievement: Achievement) : Achievement? {
        if (isUnlocked(achievement)) return null
        save(achievement)
        return achievement
    }

    @Query("SELECT * FROM achievements")
    suspend fun getAll(): List<Achievement>

    @Query("SELECT * FROM achievements WHERE id = :id AND difficulty = :difficulty AND length = :length")
    suspend fun findById(id: AchievementId, difficulty: Int?, length: Int?): Achievement?
}
