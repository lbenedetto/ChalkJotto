package com.benedetto.chalkjotto.definitions

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.database.achievement.Achievement
import com.benedetto.chalkjotto.databinding.PopupAchievementBinding
import com.benedetto.chalkjotto.definitions.Sound.penClickDownSound

/**
 * Global achievement popup queue. Decoupled from any fragment or activity lifecycle.
 *
 * Usage:
 *   - Call enqueue() from anywhere (any thread) to add achievements to the queue.
 *   - Call setTarget() in Activity.onResume / onPause(null) so the popup
 *     always shows in the foreground activity's root, regardless of fragment transitions.
 */
object AchievementPopup {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val pending = ArrayDeque<Achievement>()
    private var target: ViewGroup? = null
    private var isShowing = false

    /** Register the root ViewGroup that popups will be added to. */
    fun setTarget(root: ViewGroup?) {
        target = root
        if (root != null) {
            mainHandler.postDelayed(::drainQueue, 400)
        }
    }

    /** Enqueue achievements to display; shows immediately if a target is set. */
    fun enqueue(achievements: List<Achievement>) {
        if (achievements.isEmpty()) return
        mainHandler.post {
            pending.addAll(achievements)
            drainQueue()
        }
    }

    private fun drainQueue() {
        if (isShowing || target == null || pending.isEmpty()) return
        val achievement = pending.removeFirst()
        isShowing = true
        showOne(target!!, achievement) {
            isShowing = false
            mainHandler.postDelayed(::drainQueue, 300)
        }
    }

    private fun showOne(root: ViewGroup, achievement: Achievement, onDone: () -> Unit) {
        val statusBarInset = ViewCompat.getRootWindowInsets(root)
            ?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0

        val decor = root.rootView as ViewGroup
        val binding = PopupAchievementBinding.inflate(LayoutInflater.from(root.context), decor, false)

        binding.composeAchievementIcon.setContent {
            AchievementCell(
                achievementId = achievement.id,
                difficulty = achievement.difficulty,
                length = achievement.length,
                isUnlocked = true,
                modifier = Modifier.fillMaxSize()
            )
        }

        binding.textAchievementTitle.text = achievement.shortDescription(root.context)
        binding.textAchievementDescription.text = achievement.longDescription(root.context)
        binding.textAchievementUnlocked.text = root.context.getString(R.string.achievement_unlocked)

        decor.addView(binding.root)

        penClickDownSound()
        mainHandler.postDelayed(Sound::penClickUpSound, 200)

        binding.root.doOnLayout {
            val statusBarY = statusBarInset.toFloat()
            val height = binding.root.measuredHeight
            binding.root.translationY = 0f - statusBarY - height
            binding.root.animate()
                .translationY(statusBarY)
                .setDuration(400)
                .setInterpolator(FastOutSlowInInterpolator())
                .withEndAction {
                    binding.root.postDelayed({
                        binding.root.animate()
                            .translationX(binding.root.width.toFloat())
                            .alpha(0f)
                            .setDuration(300)
                            .setInterpolator(FastOutSlowInInterpolator())
                            .withEndAction {
                                decor.removeView(binding.root)
                                onDone()
                            }
                            .start()
                    }, 3500)
                }
                .start()
        }
    }
}
