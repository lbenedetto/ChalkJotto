package com.benedetto.chalkjotto.definitions

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

class ConfigurableOnTouchListener : View.OnTouchListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        behaviors.forEach { behavior ->
            behavior.onTouch(v, event)
        }
        return false
    }

    private var behaviors = ArrayList<View.OnTouchListener>()

    fun addBehavior(behavior: View.OnTouchListener): ConfigurableOnTouchListener {
        if (behavior is ConfigurableOnTouchListener) behaviors.addAll(behavior.behaviors)
        else behaviors.add(behavior)
        return this
    }

}