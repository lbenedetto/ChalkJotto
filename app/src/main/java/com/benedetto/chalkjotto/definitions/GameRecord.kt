package com.benedetto.chalkjotto.definitions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,    // epoch millis
    val difficulty: Int,    // 0=Normal, 1=Hard, 2=Insane
    val wordLength: Int,    // 4–7
    val numGuesses: Long,
    val numSeconds: Long,
    val didWin: Boolean
)
