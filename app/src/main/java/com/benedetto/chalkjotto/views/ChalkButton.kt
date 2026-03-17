package com.benedetto.chalkjotto.views

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.benedetto.chalkjotto.R

class ChalkButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var backgroundImage: ImageView
    private var label: TextView

    init {
        inflate(context, R.layout.view_chalk_button, this)
        backgroundImage = findViewById(R.id.chalkButtonBackground)
        label = findViewById(R.id.chalkButtonText)

        // Apply the chalkButton drawable from the theme by default
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(R.attr.chalkButton))
        val chalkButtonDrawable = typedArray.getDrawable(0)
        typedArray.recycle()
        backgroundImage.setImageDrawable(chalkButtonDrawable)

        if (attrs != null) {
            val attrs = context.obtainStyledAttributes(attrs, R.styleable.ChalkButton, defStyleAttr, 0)
            try {
                consumeAttributes(attrs)
            } finally {
                attrs.recycle()
            }
        }
    }

    private fun consumeAttributes(attrs: TypedArray) {
        val text = attrs.getText(R.styleable.ChalkButton_android_text)
        if (text != null) label.text = text

        if (attrs.hasValue(R.styleable.ChalkButton_android_textSize)) {
            label.textSize = attrs.getDimension(
                R.styleable.ChalkButton_android_textSize,
                label.textSize
            ) / resources.displayMetrics.scaledDensity
        }

        if (attrs.hasValue(R.styleable.ChalkButton_android_textColor)) {
            label.setTextColor(attrs.getColorStateList(R.styleable.ChalkButton_android_textColor))
        }

        if (attrs.hasValue(R.styleable.ChalkButton_android_src)) {
            backgroundImage.setImageDrawable(attrs.getDrawable(R.styleable.ChalkButton_android_src))
        }

        if (attrs.hasValue(R.styleable.ChalkButton_android_backgroundTint)) {
            backgroundImage.imageTintList = attrs.getColorStateList(R.styleable.ChalkButton_android_backgroundTint)
        }
    }

    var text: CharSequence
        get() = label.text
        set(value) {
            label.text = value
        }

    override fun setBackgroundTintList(tint: ColorStateList?) {
        backgroundImage.imageTintList = tint
    }
}
