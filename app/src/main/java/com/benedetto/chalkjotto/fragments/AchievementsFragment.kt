package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.database.AppDatabase
import com.benedetto.chalkjotto.database.achievement.Achievement
import com.benedetto.chalkjotto.database.achievement.AchievementId
import com.benedetto.chalkjotto.definitions.Difficulty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val lengths = listOf(4, 5, 6, 7)

class AchievementsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())

        var unlocked by mutableStateOf(emptySet<Achievement>())

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val result = AppDatabase.getInstance(requireContext())
                .achievementDao()
                .getAll()
                .toSet()
            launch(Dispatchers.Main) { unlocked = result }
        }

        composeView.setContent {
            AchievementsScreen(unlocked)
        }

        return composeView
    }
}

@Composable
private fun AchievementsScreen(unlocked: Set<Achievement>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.achievements_title),
            fontSize = 24.sp,
            color = colorResource(R.color.white),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        SectionHeader(R.string.special)
        SpecialRow(unlocked)

        listOf(
            AchievementId.WIN,
            AchievementId.EFFICIENT,
            AchievementId.FAST
        ).forEach { achievementId ->
            SectionHeader(achievementId.shortDescription)
            AchievementGrid(achievementId, unlocked)
        }
    }
}

@Composable
private fun SectionHeader(text: Int) {
    Text(
        text = stringResource(text),
        fontSize = 16.sp,
        color = colorResource(R.color.white),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 6.dp)
    )
}

/**
 * Grid layout:
 *
 *         [  4  ][  5  ][  6  ][  7  ]
 * Normal  [ icon][ icon][ icon][ icon]
 * Hard    [ icon][ icon][ icon][ icon]
 * Insane  [ icon][ icon][ icon][ icon]
 */
@Composable
private fun AchievementGrid(tier: AchievementId, unlocked: Set<Achievement>) {
    val labelWidth = 64.dp

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        // Header row: blank corner + length labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(labelWidth))
            for (len in lengths) {
                Text(
                    text = len.toString(),
                    fontSize = 13.sp,
                    color = colorResource(R.color.white),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Difficulty.entries.forEach { difficulty ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(difficulty.displayName),
                    fontSize = 13.sp,
                    color = colorResource(R.color.white),
                    modifier = Modifier.width(labelWidth)
                )
                for (len in lengths) {
                    AchievementCell(
                        achievementId = tier,
                        difficulty = difficulty.ordinal,
                        length = len,
                        isUnlocked = unlocked.contains(Achievement(tier, difficulty.ordinal, len)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialRow(unlocked: Set<Achievement>) {
    val achievements = AchievementId.entries.filter { it.isUnique }
        .map { Achievement(it) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        achievements.forEach { achievement ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AchievementCell(
                    achievementId = achievement.id,
                    difficulty = -1,
                    length = -1,
                    isUnlocked = unlocked.contains(achievement),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(achievement.id.shortDescription),
                    fontSize = 12.sp,
                    color = colorResource(R.color.white),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// difficulty color: 0=Normal(green), 1=Hard(orange), 2=Insane(red)
private val difficultyTierColor = mapOf(
    0 to R.color.colorLetterYes,
    1 to R.color.colorLetterMaybe,
    2 to R.color.colorLetterNo,
)

// tier layers added per length: length 4=[], 5=[t1], 6=[t1,t2], 7=[t1,t2,t3]
private val coloredTierLayers = listOf(
    R.drawable.achievement_t1,
    R.drawable.achievement_t2,
    R.drawable.achievement_t3,
)

@Composable
private fun AchievementCell(
    achievementId: AchievementId,
    difficulty: Int,
    length: Int,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val alpha = if (isUnlocked) 1f else 0.25f
    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // Background
        Image(
            painter = painterResource(R.drawable.key_white),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        // Base achievement image
        val iconScale = Modifier.fillMaxSize(0.90f)
        val baseIcon = if (isUnlocked) achievementId.baseDrawable else R.drawable.achievement_unknown
        Image(
            painter = painterResource(baseIcon),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alpha = alpha,
            modifier = iconScale
        )

        if (achievementId.isUnique || !isUnlocked) return@Box
        // Colored tier layers: length 5 → t1, length 6 → t1+t2, length 7 → t1+t2+t3
        val numColoredTiers = (length - 3).coerceIn(1, 3)
        val tierColor = difficultyTierColor[difficulty]?.let { colorResource(it) } ?: Color.Unspecified
        for (i in 0 until numColoredTiers) {
            Image(
                painter = painterResource(coloredTierLayers[i]),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(tierColor),
                alpha = alpha,
                modifier = iconScale
            )
        }
        if (length == 7) {
            // Top cap layer (t4)
            Image(
                painter = painterResource(R.drawable.achievement_t4),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                alpha = alpha,
                modifier = iconScale
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun AchievementsScreenPreview() {
    val unlocked = setOf(
        Achievement(AchievementId.WIN, difficulty = 0, length = 4),
        Achievement(AchievementId.WIN, difficulty = 0, length = 5),
        Achievement(AchievementId.EFFICIENT, difficulty = 1, length = 6),
        Achievement(AchievementId.FAST, difficulty = 2, length = 7),
        Achievement(AchievementId.READ_TUTORIAL),
        Achievement(AchievementId.COMPLETE_LESSON_1)
    )
    AchievementsScreen(unlocked)
}
