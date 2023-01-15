package com.benedetto.chalkjotto.definitions

import android.widget.TextView
import com.benedetto.chalkjotto.R

enum class KeyState(val background: Int) {
    BLANK(R.drawable.key_white),
    MAYBE(R.drawable.key_yellow),
    MAYBE_BLUE(R.drawable.key_blue),
    MAYBE_PINK(R.drawable.key_pink),
    YES(R.drawable.key_green),
    NO(R.drawable.key_red)
}

data class Key(
        val letter: String,
        val view: TextView,
        var state: KeyState,
        val listeners: ArrayList<TextView>) {
    constructor(letters: String, view: TextView) : this(letters, view, KeyState.BLANK, ArrayList())

    fun addListener(textView: TextView) {
        listeners.add(textView)
    }

    fun removeListener(textView: TextView) {
        listeners.remove(textView)
    }

    private fun notifyListeners() {
        listeners.forEach { listener ->
            listener.setBackgroundResource(state.background)
        }
    }

    fun updateState(newState: KeyState) {
        state = newState
        view.setBackgroundResource(state.background)
        notifyListeners()
    }
}