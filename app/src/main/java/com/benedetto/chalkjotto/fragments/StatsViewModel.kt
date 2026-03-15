package com.benedetto.chalkjotto.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.benedetto.chalkjotto.definitions.AppDatabase
import com.benedetto.chalkjotto.definitions.GameRecord
import com.benedetto.chalkjotto.definitions.GuessHistogramEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class LinearTrend(val x0: Double, val y0: Double, val x1: Double, val y1: Double)

data class StatsUiState(
    val totalGames: Int = 0,
    val winRatePct: Int = 0,
    val avgGuesses: Float? = null,
    val avgSeconds: Float? = null,
    val guessHistogram: List<GuessHistogramEntry> = emptyList(),
    val trendRecords: List<GameRecord> = emptyList(),
    val guessesTrend: LinearTrend? = null,
    val timeTrend: LinearTrend? = null,
)

fun linearTrend(values: List<Double>): LinearTrend? {
    if (values.size < 2) return null
    val n = values.size.toDouble()
    val xMean = (values.indices.sumOf { it.toDouble() }) / n
    val yMean = values.sum() / n
    val num = values.indices.sumOf { (it - xMean) * (values[it] - yMean) }
    val den = values.indices.sumOf { (it - xMean) * (it - xMean) }
    if (den == 0.0) return null
    val slope = num / den
    val intercept = yMean - slope * xMean
    val x0 = 0.0
    val x1 = values.size - 1.0
    return LinearTrend(x0, intercept + slope * x0, x1, intercept + slope * x1)
}

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val difficulty = MutableStateFlow<Int?>(null)
    private val wordLength = MutableStateFlow<Int?>(null)

    val selectedDifficulty: Int? get() = difficulty.value
    val selectedWordLength: Int? get() = wordLength.value

    private val dao = AppDatabase.getInstance(application).gameRecordDao()

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState

    init {
        refresh()
    }

    fun setDifficulty(value: Int?) {
        difficulty.value = value
        refresh()
    }

    fun setWordLength(value: Int?) {
        wordLength.value = value
        refresh()
    }

    private fun refresh() {
        val d = difficulty.value
        val l = wordLength.value
        viewModelScope.launch(Dispatchers.IO) {
            val summary = dao.getSummary(d, l)
            val histogram = dao.getGuessHistogram(d, l)
            val trend = dao.getRecentRecords(d, l).reversed()
            val winRatePct = if (summary.totalGames > 0) (summary.wins * 100) / summary.totalGames else 0
            _uiState.value = StatsUiState(
                totalGames = summary.totalGames,
                winRatePct = winRatePct,
                avgGuesses = summary.avgGuesses,
                avgSeconds = summary.avgSeconds,
                guessHistogram = histogram,
                trendRecords = trend,
                guessesTrend = linearTrend(trend.map { it.numGuesses.toDouble() }),
                timeTrend = linearTrend(trend.map { it.numSeconds.toDouble() }),
            )
        }
    }

    fun seedTestData() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val dayMs = 24 * 60 * 60 * 1000L
            repeat(50) { i ->
                dao.insert(
                    GameRecord(
                        timestamp = now - i * dayMs / 3, // progressively older data
                        difficulty = Random.nextInt(3),
                        wordLength = Random.nextInt(4, 8),
                        numGuesses = randomLongBinomial(3, 30L + i),
                        numSeconds = randomLongBinomial(30, 60L + (i * 5)),
                        didWin = Random.nextFloat() > 0.3f
                    )
                )
            }
            refresh()
        }
    }

    private fun randomLongBinomial(start: Long, end: Long): Long {
        val s = start / 3
        val e = end / 3
        return Random.nextLong(s, e) + Random.nextLong(s, e) + Random.nextLong(s, e)
    }
}
