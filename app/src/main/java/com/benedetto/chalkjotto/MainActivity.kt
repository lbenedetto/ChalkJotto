package com.benedetto.chalkjotto

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.databinding.ActivityMainBinding
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound
import com.benedetto.chalkjotto.fragments.TitleFragment
import com.benedetto.chalkjotto.fragments.TutorialFragment

//https://www.deviantart.com/mattiamc/art/ChalkBoard-Texture-MC2015-506107812
const val TutorialTag = "Tutorial"
const val TitleTag = "Title"

class MainActivity : JottoActivity() {

    private var activeFragmentTag: String = TitleTag
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DataManager.init(this)
        Sound.init(this)

        if (savedInstanceState != null) {
            return
        }

        goToFragment(TitleTag)
    }

    fun goToFragment(tag: String) {
        activeFragmentTag = tag
        goToFragment(getFragmentInstance(tag))
    }

    fun goToFragment(fragment: Fragment) {
        setTheme(R.style.AppTheme)
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment, fragment.tag)
                .addToBackStack(fragment.tag)
                .commit()
    }

    private fun getFragmentInstance(tag: String): Fragment {
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = when (tag) {
                TutorialTag -> TutorialFragment()
                else -> TitleFragment()
            }
        }
        return fragment
    }

    override fun onResume() {
        super.onResume()
        DataManager.init(this)
        if (activeFragmentTag == TitleTag && !DataManager.hasSeenRatingPrompt && DataManager.wonGames == 5) {
            DataManager.hasSeenRatingPrompt = true
            Toast.makeText(this, "You seem to be enjoying the game :) Please consider leaving a rating <3", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        if (activeFragmentTag != TitleTag)
            super.onBackPressed()
    }
}
