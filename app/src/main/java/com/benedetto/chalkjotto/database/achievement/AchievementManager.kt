package com.benedetto.chalkjotto.database.achievement

import com.benedetto.chalkjotto.game.GameState

object AchievementManager {

    suspend fun checkAndUnlock(gameState: GameState, dao: AchievementDao): List<Achievement> {
        if (!gameState.allowSettingRecords) return emptyList()

        if (!gameState.didWin && gameState.numGuesses >= 1 && gameState.hasDeductions()) {
            return listOfNotNull(awardSimple(AchievementId.GIVE_UP, dao))
        }

        if (!gameState.didWin) return emptyList()

        val unlocked = mutableListOf<Achievement>()

        unlocked.add(Achievement.create(AchievementId.WIN, gameState))

        if (gameState.numSeconds <= AchievementId.FAST.threshold(gameState.wordDifficulty, gameState.wordLength)) {
            unlocked.add(Achievement.create(AchievementId.FAST, gameState))
        }
        if (gameState.numGuesses <= AchievementId.EFFICIENT.threshold(gameState.wordDifficulty, gameState.wordLength)) {
            unlocked.add(Achievement.create(AchievementId.EFFICIENT, gameState))
        }
        if (gameState.numGuesses == 1L) {
            unlocked.add(Achievement(AchievementId.LUCKY))
        }

        return unlocked.mapNotNull { dao.unlock(it) }
    }

    suspend fun awardSimple(id: AchievementId, dao: AchievementDao): Achievement? {
        return dao.unlock(Achievement(id))
    }
}
