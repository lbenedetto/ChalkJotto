package com.benedetto.chalkjotto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.benedetto.chalkjotto.databinding.ActivityMainBinding
import com.benedetto.chalkjotto.definitions.AppDatabase
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.fitToWindowInsets
import com.benedetto.chalkjotto.fragments.*
import com.benedetto.chalkjotto.game.GameActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//https://www.deviantart.com/mattiamc/art/ChalkBoard-Texture-MC2015-506107812
const val TutorialTag = "Tutorial"
const val TitleTag = "Title"
const val ShareChallengeTag = "ShareChallenge"
const val AcceptChallengeTag = "AcceptChallenge"
const val AboutTag = "About"
const val LearnTag = "Learn"
const val StatsTag = "Stats"

class MainActivity : JottoActivity() {

    private var activeFragmentTag = MutableLiveData(TitleTag)
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.fitToWindowInsets()

        val payload = intent.data?.getQueryParameter("payload")
        when {
            payload != null -> goToFragment(AcceptChallengeTag)
            DataManager.isGameInProgress -> {
                registerForActivityResult(GameActivity.Contract()) {
                    goToFragment(it.destination)
                }.launch(null)
            }

            savedInstanceState != null -> return
            else -> goToFragment(TitleTag)
        }

        val doNothingCallback = onBackPressedDispatcher.addCallback(this) {}
        activeFragmentTag.observe(this) {
            doNothingCallback.isEnabled = it == TitleTag
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val payload = intent?.data?.getQueryParameter("payload")
        if (payload != null) {
            setIntent(intent)
            goToFragment(AcceptChallengeTag)
        }
    }

    fun goToFragment(tag: String) {
        activeFragmentTag.postValue(tag)
        goToFragment(getFragmentInstance(tag))
    }

    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment, fragment.tag)
            .addToBackStack(fragment.tag)
            .commit()
    }

    private fun getFragmentInstance(tag: String): Fragment {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null) return fragment
        return when (tag) {
            TutorialTag -> TutorialFragment()
            ShareChallengeTag -> ShareChallengeFragment()
            AcceptChallengeTag -> AcceptChallengeFragment()
            AboutTag -> AboutFragment()
            LearnTag -> LearnFragment()
            StatsTag -> StatsFragment()
            else -> TitleFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        if (activeFragmentTag.value != TitleTag || DataManager.hasSeenRatingPrompt) return
        lifecycleScope.launch(Dispatchers.IO) {
            val wins = AppDatabase.getInstance(this@MainActivity).gameRecordDao().getWinCount()
            if (wins < 5) return@launch
            launch(Dispatchers.Main) {
                DataManager.hasSeenRatingPrompt = true
                Toast.makeText(
                    this@MainActivity,
                    "You seem to be enjoying the game :) Please consider leaving a rating <3",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
