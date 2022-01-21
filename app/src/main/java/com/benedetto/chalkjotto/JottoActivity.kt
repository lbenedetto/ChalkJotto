package com.benedetto.chalkjotto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound

abstract class JottoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataManager.init(this)
        Sound.init(this)
    }

    override fun onResume() {
        super.onResume()
        DataManager.init(this)
        Sound.init(this)
    }
}