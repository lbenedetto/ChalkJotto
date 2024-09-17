package com.benedetto.chalkjotto.definitions

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import com.benedetto.chalkjotto.R

enum class KeyState(private val background: Int) {
    BLANK(R.attr.chalkKeyWhite),
    MAYBE(R.attr.chalkKeyYellow),
    MAYBE_BLUE(R.attr.chalkKeyBlue),
    MAYBE_PINK(R.attr.chalkKeyPink),
    YES(R.attr.chalkKeyGreen),
    NO(R.attr.chalkKeyRed),
    ;

    fun getBackgroundResource(context: Context): Int {
        val outValue = TypedValue()
        context.theme.resolveAttribute(background, outValue, true)
        return outValue.resourceId
    }
}

data class Key(
    val letter: String,
    val view: TextView,
    var state: KeyState,
    val listeners: ArrayList<TextView>
) {
    constructor(letters: String, view: TextView) : this(letters, view, KeyState.BLANK, ArrayList())

    fun addListener(textView: TextView) {
        listeners.add(textView)
    }

    fun removeListener(textView: TextView) {
        listeners.remove(textView)
    }

    private fun notifyListeners() {
        listeners.forEach { listener ->
            listener.setBackgroundResource(state.getBackgroundResource(view.context))
        }
    }

    fun updateState(newState: KeyState) {
        state = newState
        view.setBackgroundResource(state.getBackgroundResource(view.context))
        notifyListeners()
    }
}