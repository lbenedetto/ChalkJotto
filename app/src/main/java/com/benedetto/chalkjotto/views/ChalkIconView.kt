package com.benedetto.chalkjotto.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.benedetto.chalkjotto.R
import androidx.core.graphics.createBitmap
import kotlin.math.floor

class ChalkIconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var chalkSeed: Int
    private var chalkScale: Float
    private var chalkOctaves: Int
    private var chalkPersistence: Float
    private var chalkLacunarity: Float

    @Volatile
    private var offscreenBitmap: Bitmap? = null
    private var computing = false
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ChalkIconView, 0, 0).apply {
            try {
                chalkSeed = getInt(R.styleable.ChalkIconView_chalkSeed, 42)
                chalkScale = getFloat(R.styleable.ChalkIconView_chalkScale, 10f)
                chalkOctaves = getInt(R.styleable.ChalkIconView_chalkOctaves, 6)
                chalkPersistence = getFloat(R.styleable.ChalkIconView_chalkPersistence, 0.5f)
                chalkLacunarity = getFloat(R.styleable.ChalkIconView_chalkLacunarity, 2.0f)
            } finally {
                recycle()
            }
        }
        scaleType = ScaleType.FIT_CENTER
        offscreenBitmap = null
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        offscreenBitmap = null
        computing = false
        scheduleChalkBitmap(w, h)
    }

    private fun scheduleChalkBitmap(w: Int, h: Int) {
        val d = drawable ?: return
        if (w <= 0 || h <= 0 || computing) return
        val drawableCopy = d.constantState?.newDrawable(resources)?.mutate() ?: return
        computing = true
        val contentW = w - paddingLeft - paddingRight
        val contentH = h - paddingTop - paddingBottom
        if (contentW <= 0 || contentH <= 0) return
        Thread {
            val bmp = buildChalkBitmap(drawableCopy, contentW, contentH)
            post {
                offscreenBitmap = bmp
                computing = false
                invalidate()
            }
        }.start()
    }

    override fun onDraw(canvas: Canvas) {
        val bmp = offscreenBitmap
        if (bmp != null) {
            canvas.drawBitmap(bmp, paddingLeft.toFloat(), paddingTop.toFloat(), bitmapPaint)
        } else {
            super.onDraw(canvas)
        }
    }

    private fun buildChalkBitmap(d: Drawable, w: Int, h: Int): Bitmap {
        val bmp = createBitmap(w, h)
        val offCanvas = Canvas(bmp)
        d.setBounds(0, 0, w, h)
        d.draw(offCanvas)

        val pixels = IntArray(w * h)
        bmp.getPixels(pixels, 0, w, 0, 0, w, h)

        // Edge erosion: find pixels near transparent boundaries
        val edgeDist = IntArray(w * h)
        val erosion = 3
        // Forward pass
        for (y in 0 until h) {
            for (x in 0 until w) {
                val idx = y * w + x
                if ((pixels[idx] ushr 24) == 0 || x == 0 || y == 0 || x == w - 1 || y == h - 1) {
                    edgeDist[idx] = 0
                } else {
                    edgeDist[idx] = minOf(erosion + 1, edgeDist[idx - 1] + 1, edgeDist[idx - w] + 1)
                }
            }
        }
        // Backward pass
        for (y in h - 1 downTo 0) {
            for (x in w - 1 downTo 0) {
                val idx = y * w + x
                if (edgeDist[idx] == 0) continue
                if (x < w - 1) edgeDist[idx] = minOf(edgeDist[idx], edgeDist[idx + 1] + 1)
                if (y < h - 1) edgeDist[idx] = minOf(edgeDist[idx], edgeDist[idx + w] + 1)
            }
        }

        val perm = buildPermutation(chalkSeed)
        val seedOffset = chalkSeed * 31.7
        val pixelScale = maxOf(w, h) / chalkScale
        val rng = java.util.Random(chalkSeed.toLong())

        for (idx in pixels.indices) {
            val alpha = pixels[idx] ushr 24
            if (alpha == 0) continue

            val x = idx % w
            val y = idx / w
            val n = perlin2d(
                x / pixelScale + seedOffset,
                y / pixelScale + seedOffset,
                chalkOctaves, chalkPersistence, chalkLacunarity, perm
            )
            val trend = ((n + 1f) * 0.5f).coerceIn(0f, 1f)
            val grain = rng.nextFloat()
            val combined = (trend * 0.7f + grain * 0.3f)
            var factor = (combined * 2.5f - 0.75f).coerceIn(0f, 1f)

            // Erode edges: pixels near transparent boundaries get extra alpha reduction
            val dist = edgeDist[idx]
            if (dist <= erosion) {
                val edgeFade = dist.toFloat() / erosion
                factor *= edgeFade
            }

            val newAlpha = minOf(255, (alpha * factor * 1.5f).toInt())
            pixels[idx] = (pixels[idx] and 0x00FFFFFF) or (newAlpha shl 24)
        }

        bmp.setPixels(pixels, 0, w, 0, 0, w, h)
        return bmp
    }

    companion object {
        // Perlin noise implementation

        private fun buildPermutation(seed: Int): IntArray {
            val p = IntArray(256) { it }
            // Fisher-Yates shuffle with seed
            val rng = java.util.Random(seed.toLong())
            for (i in 255 downTo 1) {
                val j = rng.nextInt(i + 1)
                val tmp = p[i]; p[i] = p[j]; p[j] = tmp
            }
            // Double the table to avoid index wrapping
            return IntArray(512) { p[it and 255] }
        }

        private fun fade(t: Double): Double = t * t * t * (t * (t * 6.0 - 15.0) + 10.0)

        private fun lerp(a: Double, b: Double, t: Double): Double = a + t * (b - a)

        private fun grad(hash: Int, x: Double, y: Double): Double {
            return when (hash and 3) {
                0 -> x + y
                1 -> -x + y
                2 -> x - y
                else -> -x - y
            }
        }

        private fun perlinNoise(x: Double, y: Double, perm: IntArray): Double {
            val xi = floor(x).toInt() and 255
            val yi = floor(y).toInt() and 255
            val xf = x - floor(x)
            val yf = y - floor(y)
            val u = fade(xf)
            val v = fade(yf)

            val aa = perm[perm[xi] + yi]
            val ab = perm[perm[xi] + yi + 1]
            val ba = perm[perm[xi + 1] + yi]
            val bb = perm[perm[xi + 1] + yi + 1]

            return lerp(
                lerp(grad(aa, xf, yf), grad(ba, xf - 1.0, yf), u),
                lerp(grad(ab, xf, yf - 1.0), grad(bb, xf - 1.0, yf - 1.0), u),
                v
            )
        }

        private fun perlin2d(
            x: Double, y: Double,
            octaves: Int, persistence: Float, lacunarity: Float,
            perm: IntArray
        ): Float {
            var total = 0.0
            var amplitude = 1.0
            var frequency = 1.0
            var maxAmplitude = 0.0

            repeat(octaves) {
                total += perlinNoise(x * frequency, y * frequency, perm) * amplitude
                maxAmplitude += amplitude
                amplitude *= persistence
                frequency *= lacunarity
            }

            return (total / maxAmplitude).toFloat()
        }

    }
}
