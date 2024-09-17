package com.benedetto.chalkjotto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound

abstract class JottoActivity : AppCompatActivity() {

    private var themeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataManager.init(this)
        Sound.init(this)
        themeUpdated(recreate = false)
    }

    fun themeUpdated(recreate: Boolean) {
        val newTheme = if (DataManager.highContrastModeEnabled) {
            R.style.HighContrastTheme
        } else {
            R.style.AppTheme
        }

        setTheme(newTheme)
        if (recreate || themeId != null && themeId != newTheme) {
            recreate()
        }
        themeId = newTheme

    }

    override fun onResume() {
        super.onResume()
        DataManager.init(this)
        Sound.init(this)
        themeUpdated(recreate = false)
    }
}