package com.benedetto.chalkjotto

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.dialogs.showNewGameDialog
import com.benedetto.chalkjotto.dialogs.showSettingsDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import kotlinx.android.synthetic.main.activity_title.*

//https://www.deviantart.com/mattiamc/art/ChalkBoard-Texture-MC2015-506107812
//https://developers.google.com/games/services/common/concepts/savedgames
class TitleActivity : AppCompatActivity() {
	lateinit var gamesManager: PlayGamesManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_title)
		DataManager.init(this)
		Sound.init(this)
		window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
		gamesManager = PlayGamesManager(this)

		buttonNewGame.setOnClickListener { _ ->
			if (!gamesManager.isSignedIn() && DataManager.autoSignIn) {
				gamesManager.signInSilently()
			}
			showNewGameDialog(this)
		}

		buttonSettings.setOnClickListener { _ ->
			showSettingsDialog(this)
		}

		buttonAchievements.setOnClickListener { _ ->
			gamesManager.doIfInitialized {
				Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
						.achievementsIntent
						.addOnSuccessListener { intent -> startActivityForResult(intent, 157) }
			}
		}

		buttonLeaderboards.setOnClickListener {
			gamesManager.doIfInitialized {
				Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
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
