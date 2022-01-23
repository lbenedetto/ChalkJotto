package com.benedetto.chalkjotto.definitions

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.benedetto.chalkjotto.R
import com.google.android.gms.common.util.Base64Utils
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


operator fun View.OnTouchListener.plus(other: View.OnTouchListener): View.OnTouchListener {
    return ConfigurableOnTouchListener().addBehavior(this).addBehavior(other)
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun secondsToTimeDisplay(numSeconds: Long): String {
    return String.format("%02d:%02d", (numSeconds % 3600) / 60, numSeconds % 60)
}

fun animatePopIn(view: View) {
    val zoomIn = AnimationUtils.loadAnimation(view.context, R.anim.zoom_into_place)
    view.clearAnimation()
    view.startAnimation(zoomIn)
}

fun newBlankTile(context: Context): TextView {
    val tile = TextView(context)
    tile.setTextColor(ContextCompat.getColor(context, android.R.color.white))
    tile.typeface = ResourcesCompat.getFont(context, R.font.architects_daughter)
    tile.textSize = 34f
    tile.setBackgroundResource(KeyState.BLANK.background)
    val size = dpToPx(40)
    tile.gravity = Gravity.CENTER
    val params = ConstraintLayout.LayoutParams(size, size)
    params.topMargin = 2
    params.bottomMargin = 2
    params.leftMargin = 2
    params.rightMargin = 2
    tile.layoutParams = params
    tile.elevation = dpToPx(2).toFloat()

    return tile
}

private const val SECRET_KEY = "aesEncryptionKey"
private const val INIT_VECTOR = "encryptionIntVec"
val IvParameterSpec = IvParameterSpec(INIT_VECTOR.toByteArray(charset("UTF-8")))
val SecretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(charset("UTF-8")), "AES")
val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")

fun encrypt(value: String): String {
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec, IvParameterSpec)
    val encrypted: ByteArray = cipher.doFinal(value.toByteArray())
    return Base64Utils.encode(encrypted)
}

fun decrypt(value: String): String {
    cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec, IvParameterSpec)
    val original: ByteArray = cipher.doFinal(Base64Utils.decode(value))
    return String(original)
}