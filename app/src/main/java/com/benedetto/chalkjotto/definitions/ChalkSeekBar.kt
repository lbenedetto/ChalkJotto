package com.benedetto.chalkjotto.definitions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import com.benedetto.chalkjotto.R
import androidx.core.graphics.drawable.toDrawable

class ChalkSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.seekBarStyle
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    init {
        if (!isInEditMode && !DataManager.highContrastModeEnabled) {
            progressDrawable = android.graphics.Color.TRANSPARENT.toDrawable()
        }
    }

    private val trackBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.divider)
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = PorterDuffColorFilter(0xFFF5F5F5.toInt(), PorterDuff.Mode.SRC_IN)
    }
    private val trackPaintFaded = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        alpha = 100
        colorFilter = PorterDuffColorFilter(0xFFF5F5F5.toInt(), PorterDuff.Mode.SRC_IN)
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInEditMode && !DataManager.highContrastModeEnabled) {
            drawTrack(canvas)
        }
        super.onDraw(canvas)
    }

    private fun drawTrack(canvas: Canvas) {
        val thumbOffset = thumbOffset
        val trackLeft = paddingLeft + thumbOffset
        val trackRight = width - paddingRight - thumbOffset
        val trackWidth = trackRight - trackLeft
        if (trackWidth <= 0) return

        val trackHeight = 4 * resources.displayMetrics.density
        val trackTop = (height - trackHeight) / 2f

        val progressFraction = if (max > 0) progress.toFloat() / max else 0f
        val progressX = trackLeft + progressFraction * trackWidth

        // Draw faded background (full track)
        canvas.drawBitmap(
            trackBitmap,
            Rect(0, 0, trackBitmap.width, trackBitmap.height),
            RectF(trackLeft.toFloat(), trackTop, trackRight.toFloat(), trackTop + trackHeight),
            trackPaintFaded
        )

        // Draw bright progress portion
        if (progressX > trackLeft) {
            val srcProgressWidth = ((progressX - trackLeft) / trackWidth * trackBitmap.width).toInt()
                .coerceAtLeast(1)
            canvas.drawBitmap(
                trackBitmap,
                Rect(0, 0, srcProgressWidth, trackBitmap.height),
                RectF(trackLeft.toFloat(), trackTop, progressX, trackTop + trackHeight),
                trackPaint
            )
        }
    }
}
