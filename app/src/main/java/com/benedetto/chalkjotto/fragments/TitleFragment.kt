package com.benedetto.chalkjotto.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.*
import com.benedetto.chalkjotto.definitions.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import kotlinx.android.synthetic.main.fragment_title.*


class TitleFragment : Fragment() {
	private lateinit var gamesManager: PlayGamesManager

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_title, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		val mActivity = activity!! as TitleActivity
		gamesManager = PlayGamesManager(mActivity)

		buttonNewGame.setOnClickListener { _ ->
			if (!gamesManager.isSignedIn() && DataManager.autoSignIn) {
				gamesManager.signInSilently()
			}
			if (DataManager.hasSeenTutoral)
				mActivity.goToFragment(NewGameTag)
			else
				mActivity.goToFragment(TutorialTag)
		}

		buttonSettings.setOnClickListener { _ ->
			mActivity.goToFragment(SettingsTag)
		}

		buttonAchievements.setOnClickListener { _ ->
			gamesManager.doIfInitialized {
				Games.getAchievementsClient(mActivity, GoogleSignIn.getLastSignedInAccount(mActivity)!!)
						.achievementsIntent
						.addOnSuccessListener { intent -> startActivityForResult(intent, 157) }
			}
		}

		buttonLeaderboards.setOnClickListener {
			gamesManager.doIfInitialized {
				Games.getLeaderboardsClient(mActivity, GoogleSignIn.getLastSignedInAccount(mActivity)!!)
						.allLeaderboardsIntent
						.addOnSuccessListener { intent -> startActivityForResult(intent, 158) }
			}
		}

		buttonNewGame.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
		buttonSettings.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
		buttonAchievements.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
		buttonLeaderboards.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
	}

	override fun onResume() {
		super.onResume()
		if (DataManager.autoSignIn) gamesManager.signInSilently()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		gamesManager.onActivityResult(requestCode, resultCode, data)
	}
}
