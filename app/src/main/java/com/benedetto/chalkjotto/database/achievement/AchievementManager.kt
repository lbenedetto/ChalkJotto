package com.benedetto.chalkjotto.database.achievement

import com.benedetto.chalkjotto.database.achievement.AchievementId.EFFICIENT
import com.benedetto.chalkjotto.database.achievement.AchievementId.FAST
import com.benedetto.chalkjotto.game.GameState

object AchievementManager {

    suspend fun checkAndUnlock(game: GameState, dao: AchievementDao): List<Achievement> {
        if (!game.allowSettingRecords) return emptyList()

        if (!game.didWin && game.numGuesses >= 1 && game.hasDeductions()) {
            return listOfNotNull(awardSimple(AchievementId.GIVE_UP, dao))
        }

        if (!game.didWin) return emptyList()

        val unlocked = mutableListOf<Achievement>()

        unlocked.add(Achievement.create(AchievementId.WIN, game))

        if (!FAST.isHidden && game.numSeconds <= FAST.threshold(game.wordDifficulty, game.wordLength)) {
            unlocked.add(Achievement.create(FAST, game))
        }
        if (!EFFICIENT.isHidden && game.numGuesses <= EFFICIENT.threshold(game.wordDifficulty, game.wordLength)) {
            unlocked.add(Achievement.create(EFFICIENT, game))
        }
        if (game.numGuesses == 1L) {
            unlocked.add(Achievement(AchievementId.LUCKY))
        }

        return unlocked.mapNotNull { dao.unlock(it) }
    }

    suspend fun awardSimple(id: AchievementId, dao: AchievementDao): Achievement? {
        return dao.unlock(Achievement(id))
    }
}
