package com.benedetto.chalkjotto.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.DataManager
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

    private var permTable: IntArray

    @Volatile
    private var offscreenBitmap: Bitmap? = null
    private var cachedW = -1
    private var cachedH = -1
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
        permTable = buildPermutation(chalkSeed)
        scaleType = ScaleType.FIT_CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == cachedW && h == cachedH && offscreenBitmap != null) return
        offscreenBitmap = null
        computing = false
        scheduleChalkBitmap(w, h)
    }

    private fun scheduleChalkBitmap(w: Int, h: Int) {
        if (DataManager.highContrastModeEnabled) return
        val d = drawable ?: return
        if (w <= 0 || h <= 0 || computing) return
        val drawableCopy = d.constantState?.newDrawable(resources)?.mutate() ?: return
        val contentW = w - paddingLeft - paddingRight
        val contentH = h - paddingTop - paddingBottom
        if (contentW <= 0 || contentH <= 0) return
        computing = true
        Thread {
            val bmp = buildChalkBitmap(drawableCopy, contentW, contentH)
            post {
                offscreenBitmap = bmp
                cachedW = w
                cachedH = h
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
        val erosion = 4
        val wm1 = w - 1
        val hm1 = h - 1
        // Forward pass
        var idx = 0
        for (y in 0 until h) {
            for (x in 0 until w) {
                if ((pixels[idx] ushr 24) == 0 || x == 0 || y == 0 || x == wm1 || y == hm1) {
                    edgeDist[idx] = 0
                } else {
                    edgeDist[idx] = minOf(erosion + 1, edgeDist[idx - 1] + 1, edgeDist[idx - w] + 1)
                }
                idx++
            }
        }
        // Backward pass
        idx = w * h - 1
        for (y in hm1 downTo 0) {
            for (x in wm1 downTo 0) {
                if (edgeDist[idx] != 0) {
                    if (x < wm1) edgeDist[idx] = minOf(edgeDist[idx], edgeDist[idx + 1] + 1)
                    if (y < hm1) edgeDist[idx] = minOf(edgeDist[idx], edgeDist[idx + w] + 1)
                }
                idx--
            }
        }

        val perm = permTable
        val seedOffset = chalkSeed * 31.7f
        val invPixelScale = chalkScale / maxOf(w, h)
        val invErosion = 1f / erosion
        val invMaxInt = 1f / Int.MAX_VALUE.toFloat()

        // Compute Perlin noise at reduced resolution, then bilinear interpolate
        val noiseStep = 4
        val nw = (w + noiseStep - 1) / noiseStep + 1
        val nh = (h + noiseStep - 1) / noiseStep + 1
        val noiseGrid = FloatArray(nw * nh)
        for (ny in 0 until nh) {
            val sy = ny * noiseStep
            val pyVal = sy * invPixelScale + seedOffset
            for (nx in 0 until nw) {
                val sx = nx * noiseStep
                val n = perlin2d(
                    sx * invPixelScale + seedOffset, pyVal,
                    chalkOctaves, chalkPersistence, chalkLacunarity, perm
                )
                noiseGrid[ny * nw + nx] = (n + 1f) * 0.5f
            }
        }

        val invStep = 1f / noiseStep

        // Fast xorshift32 RNG
        var rngState = chalkSeed.toLong() * 2654435761L
        if (rngState == 0L) rngState = 1L
        var rngInt = rngState.toInt()
        if (rngInt == 0) rngInt = 1

        idx = 0
        for (y in 0 until h) {
            val gy = y * invStep
            val gy0 = gy.toInt().coerceAtMost(nh - 2)
            val fy = gy - gy0
            val rowOff0 = gy0 * nw
            val rowOff1 = rowOff0 + nw
            for (x in 0 until w) {
                val alpha = pixels[idx] ushr 24
                if (alpha == 0) {
                    idx++; continue
                }

                val gx = x * invStep
                val gx0 = gx.toInt().coerceAtMost(nw - 2)
                val fx = gx - gx0
                val trend = (lerp(
                    lerp(noiseGrid[rowOff0 + gx0], noiseGrid[rowOff0 + gx0 + 1], fx),
                    lerp(noiseGrid[rowOff1 + gx0], noiseGrid[rowOff1 + gx0 + 1], fx),
                    fy
                )).coerceIn(0f, 1f)

                // Xorshift32 for fast grain
                rngInt = rngInt xor (rngInt shl 13)
                rngInt = rngInt xor (rngInt shr 17)
                rngInt = rngInt xor (rngInt shl 5)
                val grain = (rngInt ushr 1).toFloat() * invMaxInt

                val combined = trend * 0.7f + grain * 0.3f
                var factor = (combined * 2.5f - 0.75f).coerceIn(0f, 1f)

                val dist = edgeDist[idx]
                if (dist <= erosion) {
                    factor *= dist * invErosion
                }

                val newAlpha = minOf(255, (alpha * factor * 1.5f).toInt())
                pixels[idx] = (pixels[idx] and 0x00FFFFFF) or (newAlpha shl 24)
                idx++
            }
        }

        bmp.setPixels(pixels, 0, w, 0, 0, w, h)
        return bmp
    }

    companion object {

        private fun buildPermutation(seed: Int): IntArray {
            val p = IntArray(256) { it }
            val rng = java.util.Random(seed.toLong())
            for (i in 255 downTo 1) {
                val j = rng.nextInt(i + 1)
                val tmp = p[i]; p[i] = p[j]; p[j] = tmp
            }
            return IntArray(512) { p[it and 255] }
        }

        private fun fade(t: Float): Float = t * t * t * (t * (t * 6f - 15f) + 10f)

        private fun lerp(a: Float, b: Float, t: Float): Float = a + t * (b - a)

        private fun grad(hash: Int, x: Float, y: Float): Float {
            return when (hash and 3) {
                0 -> x + y
                1 -> -x + y
                2 -> x - y
                else -> -x - y
            }
        }

        private fun perlinNoise(x: Float, y: Float, perm: IntArray): Float {
            val floorX = floor(x)
            val floorY = floor(y)
            val xi = floorX.toInt() and 255
            val yi = floorY.toInt() and 255
            val xf = x - floorX
            val yf = y - floorY
            val u = fade(xf)
            val v = fade(yf)

            val pi = perm[xi]
            val pi1 = perm[xi + 1]
            val aa = perm[pi + yi]
            val ab = perm[pi + yi + 1]
            val ba = perm[pi1 + yi]
            val bb = perm[pi1 + yi + 1]

            return lerp(
                lerp(grad(aa, xf, yf), grad(ba, xf - 1f, yf), u),
                lerp(grad(ab, xf, yf - 1f), grad(bb, xf - 1f, yf - 1f), u),
                v
            )
        }

        private fun perlin2d(
            x: Float, y: Float,
            octaves: Int, persistence: Float, lacunarity: Float,
            perm: IntArray
        ): Float {
            var total = 0f
            var amplitude = 1f
            var frequency = 1f
            var maxAmplitude = 0f

            repeat(octaves) {
                total += perlinNoise(x * frequency, y * frequency, perm) * amplitude
                maxAmplitude += amplitude
                amplitude *= persistence
                frequency *= lacunarity
            }

            return total / maxAmplitude
        }

    }
}
