package com.benedetto.chalkjotto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.*
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.PenClickOnTouch
import com.benedetto.chalkjotto.definitions.ScaleOnTouch
import com.benedetto.chalkjotto.definitions.plus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import kotlinx.android.synthetic.main.fragment_title.*

class TitleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_title, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mActivity = activity!! as MainActivity

        buttonNewGame.setOnClickListener {
            if (!mActivity.gamesManager.isSignedIn() && DataManager.autoSignIn) {
                mActivity.gamesManager.signInSilently()
            }
            if (DataManager.hasSeenTutoral)
                mActivity.goToFragment(NewGameTag)
            else
                mActivity.goToFragment(TutorialTag)
        }

        buttonSettings.setOnClickListener {
            mActivity.goToFragment(SettingsTag)
        }

        buttonAchievements.setOnClickListener {
            mActivity.gamesManager.doIfInitialized {
                Games.getAchievementsClient(mActivity, GoogleSignIn.getLastSignedInAccount(mActivity)!!)
                        .achievementsIntent
                        .addOnSuccessListener { intent -> startActivityForResult(intent, 157) }
            }
        }

        buttonLeaderboards.setOnClickListener {
            mActivity.gamesManager.doIfInitialized {
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
}
