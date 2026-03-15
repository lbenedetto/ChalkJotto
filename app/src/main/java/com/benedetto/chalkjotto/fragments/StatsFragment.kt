package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.benedetto.chalkjotto.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.benedetto.chalkjotto.BuildConfig
import com.benedetto.chalkjotto.databinding.FragmentStatsBinding
import com.benedetto.chalkjotto.definitions.GameRecord
import com.benedetto.chalkjotto.definitions.GuessHistogramEntry
import com.benedetto.chalkjotto.definitions.ScaleOnTouch
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.definitions.secondsToTimeDisplay
import com.patrykandpatrick.vico.views.cartesian.CartesianChart
import com.patrykandpatrick.vico.views.cartesian.ScrollHandler
import com.patrykandpatrick.vico.views.cartesian.Zoom
import com.patrykandpatrick.vico.views.cartesian.ZoomHandler
import com.patrykandpatrick.vico.views.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.views.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.views.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.views.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.views.cartesian.data.columnSeries
import com.patrykandpatrick.vico.views.cartesian.data.lineSeries
import com.patrykandpatrick.vico.views.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.views.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.views.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.views.common.Fill
import com.patrykandpatrick.vico.views.common.component.LineComponent
import com.patrykandpatrick.vico.views.common.component.ShapeComponent
import com.patrykandpatrick.vico.views.common.component.TextComponent
import com.patrykandpatrick.vico.views.common.data.ExtraStore
import com.patrykandpatrick.vico.views.common.shape.CorneredShape
import kotlinx.coroutines.launch

private val xLabelsKey = ExtraStore.Key<List<String>>()
private val winFlagsKey = ExtraStore.Key<List<Boolean>>()

class StatsFragment : Fragment() {

    private val chartWhite by lazy { ContextCompat.getColor(requireContext(), R.color.white) }
    private val chartWhiteFaint by lazy { ContextCompat.getColor(requireContext(), R.color.whiteFaint) }
    private val colorWin by lazy { ContextCompat.getColor(requireContext(), R.color.colorLetterYes) }
    private val colorLoss by lazy { ContextCompat.getColor(requireContext(), R.color.colorLetterNo) }

    private lateinit var binding: FragmentStatsBinding
    private val viewModel: StatsViewModel by viewModels()

    private val histogramProducer = CartesianChartModelProducer()
    private val guessesProducer = CartesianChartModelProducer()
    private val timeProducer = CartesianChartModelProducer()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStatsBinding.inflate(layoutInflater, container, false)

        setupFilterChips()
        setupCharts()
        updateDiffHighlight(viewModel.selectedDifficulty)
        updateLenHighlight(viewModel.selectedWordLength)

        if (BuildConfig.DEBUG) {
            binding.tvTitle.setOnLongClickListener {
                viewModel.seedTestData()
                true
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.tvTotalGames.text = state.totalGames.toString()
                binding.tvWinRate.text = "${state.winRatePct}%"
                binding.tvAvgGuesses.text = state.avgGuesses?.let { "%.1f".format(it) } ?: "—"
                binding.tvAvgTime.text = state.avgSeconds?.let { secondsToTimeDisplay(it.toLong()) } ?: "—"
                updateHistogramChart(state.guessHistogram)
                updateGuessesTrendChart(state.trendRecords, state.guessesTrend)
                updateTimeTrendChart(state.trendRecords, state.timeTrend)
            }
        }

        return binding.root
    }

    private val diffChips by lazy {
        listOf(
            binding.chipDiffAll to null,
            binding.chipDiffNormal to 0,
            binding.chipDiffHard to 1,
            binding.chipDiffInsane to 2
        )
    }
    private val lenChips by lazy {
        listOf(
            binding.chipLenAll to null,
            binding.chipLen4 to 4,
            binding.chipLen5 to 5,
            binding.chipLen6 to 6,
            binding.chipLen7 to 7
        )
    }

    private fun updateDiffHighlight(selected: Int?) {
        diffChips.forEach { (chip, value) -> chip.alpha = if (value == selected) 1.0f else 0.5f }
    }

    private fun updateLenHighlight(selected: Int?) {
        lenChips.forEach { (chip, value) -> chip.alpha = if (value == selected) 1.0f else 0.5f }
    }

    private fun setupFilterChips() {
        diffChips.forEach { (chip, value) ->
            chip.setOnTouchListener(ScaleOnTouch)
            chip.setOnClickListener {
                tapSound()
                viewModel.setDifficulty(value)
                updateDiffHighlight(value)
            }
        }

        lenChips.forEach { (chip, value) ->
            chip.setOnTouchListener(ScaleOnTouch)
            chip.setOnClickListener {
                tapSound()
                viewModel.setWordLength(value)
                updateLenHighlight(value)
            }
        }
    }

    private fun axisLabel(): TextComponent {
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.architects_daughter)
        return TextComponent(color = chartWhite, typeface = typeface ?: android.graphics.Typeface.DEFAULT)
    }

    // PointProvider that colors each point green (win) or red (loss) via ExtraStore
    private fun winLossPointProvider() = object : LineCartesianLayer.PointProvider {
        private val winPoint = LineCartesianLayer.Point(
            ShapeComponent(fill = Fill(colorWin), shape = CorneredShape.rounded(allPercent = 100)),
            sizeDp = 10f,
        )
        private val lossPoint = LineCartesianLayer.Point(
            ShapeComponent(fill = Fill(colorLoss), shape = CorneredShape.rounded(allPercent = 100)),
            sizeDp = 10f,
        )
        override fun getPoint(
            entry: LineCartesianLayerModel.Entry,
            seriesIndex: Int,
            extraStore: ExtraStore,
        ): LineCartesianLayer.Point? {
            val isWin = extraStore.getOrNull(winFlagsKey)?.getOrNull(entry.x.toInt()) ?: true
            return if (isWin) winPoint else lossPoint
        }
        override fun getLargestPoint(extraStore: ExtraStore) = winPoint
    }

    private fun setupCharts() {
        val xLabelFormatter = CartesianValueFormatter { context, x, _ ->
            context.model.extraStore.getOrNull(xLabelsKey)?.getOrNull(x.toInt()) ?: x.toInt().toString()
        }
        val timeLabelFormatter = CartesianValueFormatter { _, x, _ ->
            secondsToTimeDisplay(x.toLong())
        }

        val axisLine = LineComponent(fill = Fill(chartWhite), thicknessDp = 1.5f)
        val axisTick = LineComponent(fill = Fill(chartWhite), thicknessDp = 1.5f)
        val guideline = LineComponent(fill = Fill(chartWhiteFaint), thicknessDp = 1f)
        val noScrollZoom = ZoomHandler(zoomEnabled = false, initialZoom = Zoom.Content, minZoom = Zoom.Content, maxZoom = Zoom.Content)

        binding.barChartGuesses.chart = CartesianChart(
            ColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    LineComponent(
                        fill = Fill(chartWhite),
                        thicknessDp = 16f,
                        shape = CorneredShape.rounded(allPercent = 20),
                        strokeFill = Fill(chartWhiteFaint),
                        strokeThicknessDp = 2f,
                    )
                )
            ),
            startAxis = VerticalAxis.start(
                label = axisLabel(),
                line = axisLine,
                tick = axisTick,
                tickLengthDp = 4f,
                guideline = guideline,
            ),
            bottomAxis = HorizontalAxis.bottom(
                label = axisLabel(),
                line = axisLine,
                tick = axisTick,
                tickLengthDp = 4f,
                valueFormatter = xLabelFormatter,
            ),
        )
        binding.barChartGuesses.modelProducer = histogramProducer
        binding.barChartGuesses.scrollHandler = ScrollHandler(scrollEnabled = false)
        binding.barChartGuesses.zoomHandler = noScrollZoom

        val dataLine = LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(chartWhiteFaint)),
            stroke = LineCartesianLayer.LineStroke.Dashed(thicknessDp = 1f, dashLengthDp = 8f, gapLengthDp = 4f),
            pointProvider = winLossPointProvider(),
        )
        val regressionLine = LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(chartWhite)),
            stroke = LineCartesianLayer.LineStroke.Continuous(thicknessDp = 2f),
        )

        binding.lineChartGuesses.chart = CartesianChart(
            LineCartesianLayer(LineCartesianLayer.LineProvider.series(dataLine, regressionLine)),
            startAxis = VerticalAxis.start(
                label = axisLabel(),
                line = axisLine,
                tick = axisTick,
                tickLengthDp = 4f,
                guideline = guideline,
            ),
        )
        binding.lineChartGuesses.modelProducer = guessesProducer
        binding.lineChartGuesses.scrollHandler = ScrollHandler(scrollEnabled = false)
        binding.lineChartGuesses.zoomHandler = noScrollZoom

        binding.lineChartTime.chart = CartesianChart(
            LineCartesianLayer(LineCartesianLayer.LineProvider.series(dataLine, regressionLine)),
            startAxis = VerticalAxis.start(
                label = axisLabel(),
                line = axisLine,
                tick = axisTick,
                tickLengthDp = 4f,
                guideline = guideline,
                valueFormatter = timeLabelFormatter,
            ),
        )
        binding.lineChartTime.modelProducer = timeProducer
        binding.lineChartTime.scrollHandler = ScrollHandler(scrollEnabled = false)
        binding.lineChartTime.zoomHandler = noScrollZoom
    }

    private fun updateHistogramChart(histogram: List<GuessHistogramEntry>) {
        if (histogram.isEmpty()) return
        viewLifecycleOwner.lifecycleScope.launch {
            histogramProducer.runTransaction {
                columnSeries { series(x = histogram.map { it.numGuesses }, y = histogram.map { it.count }) }
                extras { it[xLabelsKey] = histogram.map { e -> e.numGuesses.toString() } }
            }
        }
    }

    private fun updateGuessesTrendChart(records: List<GameRecord>, trend: LinearTrend?) {
        if (records.isEmpty()) return
        viewLifecycleOwner.lifecycleScope.launch {
            guessesProducer.runTransaction {
                lineSeries {
                    series(y = records.map { it.numGuesses })
                    if (trend != null) series(x = listOf(trend.x0, trend.x1), y = listOf(trend.y0, trend.y1))
                }
                extras { it[winFlagsKey] = records.map { r -> r.didWin } }
            }
        }
    }

    private fun updateTimeTrendChart(records: List<GameRecord>, trend: LinearTrend?) {
        if (records.isEmpty()) return
        viewLifecycleOwner.lifecycleScope.launch {
            timeProducer.runTransaction {
                lineSeries {
                    series(y = records.map { it.numSeconds })
                    if (trend != null) series(x = listOf(trend.x0, trend.x1), y = listOf(trend.y0, trend.y1))
                }
                extras { it[winFlagsKey] = records.map { r -> r.didWin } }
            }
        }
    }
}
