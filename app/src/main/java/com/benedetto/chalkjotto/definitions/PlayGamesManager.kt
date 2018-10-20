package com.benedetto.chalkjotto.definitions

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games

class PlayGamesManager(val activity: Activity) {
	private val afterSignInTasks = ArrayList<() -> Unit>()

	fun doIfInitialized(doAfterSignIn: () -> Unit) {
		if (isSignedIn()) {
			doAfterSignIn()
		} else {
			afterSignInTasks.add(doAfterSignIn)
			signInSilently()
		}
	}

	fun isSignedIn(): Boolean {
		return GoogleSignIn.getLastSignedInAccount(activity) != null
	}

	fun signOutSilently() {
		GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).signOut()
		DataManager.autoSignIn = false
	}

	fun signInSilently() {
		val signInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
		signInClient.silentSignIn().addOnCompleteListener(activity) { task ->
			if (task.isSuccessful) {
				postSignInTasks(task.result!!)
			} else {
				// Player will need to sign-in explicitly using via UI
				startSignInIntent()
			}
		}
	}

	private val RC_SIGN_IN = 156
	private fun startSignInIntent() {
		val signInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
		val intent = signInClient.signInIntent
		activity.startActivityForResult(intent, RC_SIGN_IN)
	}

	fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == RC_SIGN_IN) {
			val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
			when {
				result.isSuccess -> {
					postSignInTasks(result.signInAccount!!)
				}
				result.status.isCanceled || resultCode == 0 -> {
					//User cancelled sign in, do not auto prompt again
					DataManager.autoSignIn = false
				}
				else -> {
					var message = result.status.statusMessage
					if (message == null || message.isEmpty()) {
						message = "Could not sign into Google Play Games. Please try again later."
					}
					AlertDialog.Builder(activity).setMessage(message)
							.setNeutralButton(android.R.string.ok, null).show()
				}
			}
		}
	}

	private fun postSignInTasks(account: GoogleSignInAccount) {
		DataManager.autoSignIn = true
		Games.getGamesClient(activity, account).setViewForPopups(activity.findViewById(android.R.id.content))
		afterSignInTasks.forEach { afterSignInTask -> afterSignInTask() }
		afterSignInTasks.clear()
	}
}