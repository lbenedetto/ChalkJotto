package com.benedetto.chalkjotto.database.analytics

import com.benedetto.chalkjotto.database.gamerecord.GameRecord
import com.benedetto.chalkjotto.definitions.DataManager
import com.google.firebase.firestore.FirebaseFirestore

object AnalyticsManager {
    fun upload(record: GameRecord) {
        if (!DataManager.analyticsEnabled) return
        FirebaseFirestore.getInstance()
            .collection("game_records")
            .add(
                mapOf(
                    "difficulty" to record.difficulty,
                    "wordLength" to record.wordLength,
                    "numGuesses" to record.numGuesses,
                    "numSeconds" to record.numSeconds,
                    "didWin" to record.didWin
                )
            )
    }
}
