package com.benedetto.chalkjotto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound
import com.benedetto.chalkjotto.fragments.TitleFragment
import com.benedetto.chalkjotto.fragments.TutorialFragment

//https://www.deviantart.com/mattiamc/art/ChalkBoard-Texture-MC2015-506107812
const val TutorialTag = "Tutorial"
const val TitleTag = "Title"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        DataManager.init(this)
        Sound.init(this)

        if (savedInstanceState != null) {
            return
        }

        goToFragment(TitleTag)
    }

    fun goToFragment(tag: String) {
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

    fun getFragmentInstance(tag: String): Fragment {
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = when (tag) {
                TutorialTag -> TutorialFragment()
                else -> TitleFragment()
            }
        }
        return fragment
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        goToFragment(TitleTag)
    }
}
