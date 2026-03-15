package com.benedetto.chalkjotto.definitions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

data class StatsSummary(
    val totalGames: Int,
    val wins: Int,
    val avgGuesses: Float?,
    val avgSeconds: Float?
)

data class GuessHistogramEntry(
    val numGuesses: Long,
    val count: Int
)

@Dao
interface GameRecordDao {
    @Insert
    suspend fun insert(record: GameRecord)

    @Query("""
        SELECT
            COUNT(*) AS totalGames,
            SUM(CASE WHEN didWin = 1 THEN 1 ELSE 0 END) AS wins,
            AVG(CASE WHEN didWin = 1 THEN numGuesses ELSE NULL END) AS avgGuesses,
            AVG(CASE WHEN didWin = 1 THEN numSeconds ELSE NULL END) AS avgSeconds
        FROM game_records
        WHERE (:difficulty IS NULL OR difficulty = :difficulty)
          AND (:wordLength IS NULL OR wordLength = :wordLength)
    """)
    suspend fun getSummary(difficulty: Int?, wordLength: Int?): StatsSummary

    @Query("""
        SELECT numGuesses, COUNT(*) AS count
        FROM game_records
        WHERE didWin = 1
          AND (:difficulty IS NULL OR difficulty = :difficulty)
          AND (:wordLength IS NULL OR wordLength = :wordLength)
        GROUP BY numGuesses
        ORDER BY numGuesses ASC
    """)
    suspend fun getGuessHistogram(difficulty: Int?, wordLength: Int?): List<GuessHistogramEntry>

    @Query("""
        SELECT * FROM game_records
        WHERE (:difficulty IS NULL OR difficulty = :difficulty)
          AND (:wordLength IS NULL OR wordLength = :wordLength)
        ORDER BY timestamp DESC
        LIMIT 50
    """)
    suspend fun getRecentRecords(difficulty: Int?, wordLength: Int?): List<GameRecord>
}
