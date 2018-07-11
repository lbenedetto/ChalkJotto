package com.benedetto.chalkjotto

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.dialogs.showNewGameDialog
import com.benedetto.chalkjotto.dialogs.showSettingsDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games
import kotlinx.android.synthetic.main.activity_title.*


//https://www.deviantart.com/mattiamc/art/ChalkBoard-Texture-MC2015-506107812
//https://developers.google.com/games/services/common/concepts/savedgames
class TitleActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_title)
		DataManager.init(this)
		Sound.init(this)
		window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

		buttonNewGame.setOnClickListener { _ ->
			showNewGameDialog(this)
		}

		buttonSettings.setOnClickListener { _ ->
			showSettingsDialog(this)
		}

		buttonAchievements.setOnClickListener { _ ->
			Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
					.achievementsIntent
					.addOnSuccessListener { intent -> startActivityForResult(intent, 157) }
		}

		buttonLeaderboards.setOnClickListener {
			Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
					.allLeaderboardsIntent
					.addOnSuccessListener { intent -> startActivityForResult(intent, 158) }
		}

		buttonNewGame.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
		buttonSettings.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
		buttonAchievements.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
		buttonLeaderboards.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)

		if (!isSignedIn()) {
			signInSilently()
		}
	}

	private fun isSignedIn(): Boolean {
		return GoogleSignIn.getLastSignedInAccount(this) != null
	}

	private fun signInSilently() {
		val signInClient = GoogleSignIn.getClient(this,
				GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
		signInClient.silentSignIn().addOnCompleteListener(this) { task ->
			if (task.isSuccessful) {
				// The signed in account is stored in the task's result.
				val signedInAccount = task.result
			} else {
				// Player will need to sign-in explicitly using via UI
				startSignInIntent()
			}
		}
	}

	private val RC_SIGN_IN = 156
	private fun startSignInIntent() {
		val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
		val intent = signInClient.signInIntent
		startActivityForResult(intent, RC_SIGN_IN)
	}
}
