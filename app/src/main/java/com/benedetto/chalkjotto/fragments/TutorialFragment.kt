package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.NewGameTag
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.TitleActivity
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.tapSound
import kotlinx.android.synthetic.main.fragment_tutorial.*


class TutorialFragment : Fragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_tutorial, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		if (activity is TitleActivity) {
			buttonContinue.setOnClickListener {
				tapSound()
				DataManager.hasSeenTutoral = true
				(activity as TitleActivity).goToFragment(NewGameTag)
			}
		}
	}
}
