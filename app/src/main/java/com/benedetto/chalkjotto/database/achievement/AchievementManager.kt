package com.benedetto.chalkjotto.database.achievement

import com.benedetto.chalkjotto.game.GameState

object AchievementManager {

    // Fast threshold in seconds, keyed by (difficulty, wordLength).
    // Longer words and harder difficulties get more time.
    private val FAST_THRESHOLDS: Map<Pair<Int, Int>, Long> = mapOf(
        (0 to 4) to 60L,  (0 to 5) to 90L,  (0 to 6) to 120L, (0 to 7) to 150L,
        (1 to 4) to 90L,  (1 to 5) to 120L, (1 to 6) to 150L, (1 to 7) to 180L,
        (2 to 4) to 120L, (2 to 5) to 150L, (2 to 6) to 180L, (2 to 7) to 240L,
    )

    // Efficient threshold in guesses, keyed by (difficulty, wordLength).
    // Longer words and harder difficulties allow more guesses.
    private val EFFICIENT_THRESHOLDS: Map<Pair<Int, Int>, Long> = mapOf(
        (0 to 4) to 3L, (0 to 5) to 4L, (0 to 6) to 4L, (0 to 7) to 5L,
        (1 to 4) to 4L, (1 to 5) to 4L, (1 to 6) to 5L, (1 to 7) to 5L,
        (2 to 4) to 4L, (2 to 5) to 5L, (2 to 6) to 5L, (2 to 7) to 6L,
    )

    suspend fun checkAndUnlock(gameState: GameState, dao: AchievementDao): List<Achievement> {
        if (!gameState.didWin || !gameState.allowSettingRecords) return emptyList()

        val unlocked = mutableListOf<Achievement>()
        val key = gameState.wordDifficulty to gameState.wordLength

        unlocked.add(Achievement.create(AchievementId.WIN, gameState))

        if (gameState.numSeconds <= (FAST_THRESHOLDS[key] ?: 60L)) {
            unlocked.add(Achievement.create(AchievementId.FAST, gameState))
        }
        if (gameState.numGuesses <= (EFFICIENT_THRESHOLDS[key] ?: 3L)) {
            unlocked.add(Achievement.create(AchievementId.EFFICIENT, gameState))
        }
        if (gameState.numGuesses == 1L) {
            unlocked.add(Achievement(AchievementId.LUCKY))
        }

        return unlocked.filter { !dao.exists(it) }
            .onEach { dao.save(it) }
    }

    suspend fun awardSimple(id: AchievementId, dao: AchievementDao): Achievement? {
        return Achievement(id)
            .takeIf { !dao.exists(it) }
            ?.also { dao.save(it) }
    }
}
