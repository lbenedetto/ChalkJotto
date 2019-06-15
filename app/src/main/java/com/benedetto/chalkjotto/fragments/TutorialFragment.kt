package com.benedetto.chalkjotto.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound
import com.benedetto.chalkjotto.game.GameActivity
import kotlinx.android.synthetic.main.fragment_tutorial.*


class TutorialFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is MainActivity) {
            buttonContinue.setOnClickListener {
                tapSound()
                DataManager.hasSeenTutoral = true
                startActivityForResult(Intent(context, GameActivity::class.java), 1)
            }
        }
    }
}
