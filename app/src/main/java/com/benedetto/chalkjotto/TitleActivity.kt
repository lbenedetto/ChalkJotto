package com.benedetto.chalkjotto

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound
import com.benedetto.chalkjotto.fragments.NewGameFragment
import com.benedetto.chalkjotto.fragments.SettingsFragment
import com.benedetto.chalkjotto.fragments.TitleFragment
import com.benedetto.chalkjotto.fragments.TutorialFragment

//https://www.deviantart.com/mattiamc/art/ChalkBoard-Texture-MC2015-506107812
//https://developers.google.com/games/services/common/concepts/savedgames
const val NewGameTag = "NewGame"
const val SettingsTag = "Settings"
const val TutorialTag = "Tutorial"
const val TitleTag = "Title"

class TitleActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_title)
		DataManager.init(this)
		Sound.init(this)
		window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

		if (savedInstanceState != null) {
			return
		}

		goToFragment(TitleTag)
	}

	fun goToFragment(tag: String, entranceAnimation: Int, exitAnimation: Int) {
		supportFragmentManager.beginTransaction()
				.setCustomAnimations(entranceAnimation, exitAnimation)
				.replace(R.id.fragment_container, getFragmentInstance(tag), tag)
				.addToBackStack(tag)
				.commit()
	}

	fun goToFragment(tag: String) {
		supportFragmentManager.beginTransaction()
				.replace(R.id.fragment_container, getFragmentInstance(tag), tag)
				.addToBackStack(tag)
				.commit()
	}

	private fun getFragmentInstance(tag: String): Fragment {
		var fragment = supportFragmentManager.findFragmentByTag(tag)
		if (fragment == null) {
			fragment = when (tag) {
				NewGameTag -> NewGameFragment()
				SettingsTag -> SettingsFragment()
				TutorialTag -> TutorialFragment()
				else -> TitleFragment()
			}
		}
		return fragment
	}
}
