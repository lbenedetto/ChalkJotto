package com.benedetto.chalkjotto.definitions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.database.achievement.AchievementId

// difficulty color: 0=Normal(green), 1=Hard(orange), 2=Insane(red)
private val difficultyTierColor = mapOf(
    0 to R.color.colorLetterYes,
    1 to R.color.colorLetterMaybe,
    2 to R.color.colorLetterNo,
)

private val coloredTierLayers = listOf(
    R.drawable.achievement_t1,
    R.drawable.achievement_t2,
    R.drawable.achievement_t3,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AchievementCell(
    achievementId: AchievementId,
    difficulty: Int,
    length: Int,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
) {
    val alpha = if (isUnlocked) 1f else 0.25f
    val clickModifier = if (onLongPress != null) {
        modifier.combinedClickable(onClick = {}, onLongClick = onLongPress)
    } else modifier
    Box(
        modifier = clickModifier,
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
