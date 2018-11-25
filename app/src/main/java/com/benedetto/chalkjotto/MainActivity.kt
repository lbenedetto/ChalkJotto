package com.benedetto.chalkjotto

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.PlayGamesManager
import com.benedetto.chalkjotto.definitions.Sound
import com.benedetto.chalkjotto.fragments.NewGameFragment
import com.benedetto.chalkjotto.fragments.SettingsFragment
import com.benedetto.chalkjotto.fragments.TitleFragment
import com.benedetto.chalkjotto.fragments.TutorialFragment
import com.benedetto.chalkjotto.game.GameFragment
import kotlinx.android.synthetic.main.activity_main.*

//https://www.deviantart.com/mattiamc/art/ChalkBoard-Texture-MC2015-506107812
//https://developers.google.com/games/services/common/concepts/savedgames
const val NewGameTag = "NewGame"
const val SettingsTag = "Settings"
const val TutorialTag = "Tutorial"
const val TitleTag = "Title"
const val GameTag = "Game"

class MainActivity : AppCompatActivity() {
    var gamesManager = PlayGamesManager(this)
    private var gameFragment: GameFragment? = null
    private var statusBarSize: Int = 0
    private var navBarSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        DataManager.init(this)
        Sound.init(this)

        if (savedInstanceState != null) {
            return
        }

        goToFragment(TitleTag)
        clRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        clRoot!!.setOnApplyWindowInsetsListener { _, insets ->
            if (insets.systemWindowInsetBottom > 0) navBarSize = insets.systemWindowInsetBottom
            if (insets.systemWindowInsetTop > 0) statusBarSize = insets.systemWindowInsetTop

            (ivBottomBumper.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = navBarSize
            (ivTopBumper.layoutParams as ViewGroup.MarginLayoutParams).topMargin = statusBarSize
            gameFragment?.setInsets(navBarSize, statusBarSize)

            insets
        }

    }

    fun goToGame(wordDifficulty: Int, wordLength: Int) {
        gameFragment = getFragmentInstance(GameTag) as GameFragment
        val bundle = Bundle()
        bundle.putInt("difficulty", wordDifficulty)
        bundle.putInt("length", wordLength)
        gameFragment!!.arguments = bundle

        ivBottomBumper.animate().alpha(0f).setDuration(600).withEndAction {
            ivBottomBumper.visibility = View.GONE
        }.start()
        ivTopBumper.animate().alpha(0f).setDuration(600).withEndAction {
            ivTopBumper.visibility = View.GONE
        }.start()

        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, gameFragment!!, GameTag)
                .addToBackStack(GameTag)
                .commit()
    }

    fun goToFragment(tag: String) {
        setTheme(R.style.AppTheme)
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, getFragmentInstance(tag), tag)
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
                GameTag -> GameFragment()
                else -> TitleFragment()
            }
        }
        return fragment
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment !is GameFragment)
            super.onBackPressed()
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
