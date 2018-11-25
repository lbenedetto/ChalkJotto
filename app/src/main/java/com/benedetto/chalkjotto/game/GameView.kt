package com.benedetto.chalkjotto.game

import android.content.Context
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.benedetto.chalkjotto.R

class GameView(context: Context) : CoordinatorLayout(context) {

    init {
        inflate(context, R.layout.fragment_game, this)
    }


    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        super.onApplyWindowInsets(insets)
        setPadding(0, 0, 0, insets.systemWindowInsetBottom)
        return insets
    }


}