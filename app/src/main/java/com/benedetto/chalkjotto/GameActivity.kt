package com.benedetto.chalkjotto

//import com.google.android.gms.ads.AdRequest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.benedetto.chalkjotto.definitions.*
import com.benedetto.chalkjotto.dialogs.showColorPickerDialog
import com.benedetto.chalkjotto.dialogs.showGameOverDialog
import com.benedetto.chalkjotto.dialogs.showPauseDialog
import kotlinx.android.synthetic.main.activity_game.*
import java.io.BufferedReader
import java.io.InputStreamReader

class GameActivity : AppCompatActivity() {
	private lateinit var keys: HashMap<String, Key>
	var numSeconds = 0.toLong()
	var numGuesses = 0.toLong()
	private lateinit var mTimer: Runnable
	private var mHandler = Handler()
	private var mIsRunning = false
	var wordDifficulty = 0
	var wordLength = 5
	private var enteredWord = StringBuilder()
	lateinit var targetWord: String
	private var validWords = HashSet<String>()
	var isGameOver = false

	@SuppressLint("InflateParams", "ClickableViewAccessibility")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setTheme(R.style.AppTheme)
		setContentView(R.layout.activity_game)
		keys = getKeyHashMap(this)
		val bundle = intent.extras
		if (bundle != null) {
			wordDifficulty = bundle.getInt("difficulty")
			wordLength = bundle.getInt("length")
		}

		refillUserInputFieldWithTiles()
		val wordFile = resources.openRawResource(
				when (wordLength) {
					4 -> {
						addValidWords(R.raw.bottom_four, R.raw.middle_four, R.raw.top_four)
						when (wordDifficulty) {
							2 -> R.raw.bottom_four
							1 -> R.raw.middle_four
							else -> R.raw.top_four
						}
					}
					6 -> {
						addValidWords(R.raw.bottom_six, R.raw.middle_six, R.raw.top_six)
						when (wordDifficulty) {
							2 -> R.raw.bottom_six
							1 -> R.raw.middle_six
							else -> R.raw.top_six
						}
					}
					7 -> {
						addValidWords(R.raw.bottom_seven, R.raw.middle_seven, R.raw.top_seven)
						when (wordDifficulty) {
							2 -> R.raw.bottom_seven
							1 -> R.raw.middle_seven
							else -> R.raw.top_seven
						}
					}
					else -> {
						addValidWords(R.raw.bottom_five, R.raw.middle_five, R.raw.top_five)
						when (wordDifficulty) {
							2 -> R.raw.bottom_five
							1 -> R.raw.middle_five
							else -> R.raw.top_five
						}
					}
				}
		)

		val candidateWords = ArrayList<String>()
		BufferedReader(InputStreamReader(wordFile)).forEachLine { line ->
			candidateWords.add(line.toUpperCase())
		}
		targetWord = candidateWords[(0 until candidateWords.size).random()].split("\t")[0].toUpperCase()

		keys.forEach { (_, key) ->
			key.view.setOnTouchListener(ScaleOnTouch)
			key.view.setOnClickListener {
				tapSound()
				vibrate()
				if (enteredWord.length < wordLength) {
					val letter = layoutCorrectWord.getChildAt(enteredWord.length) as TextView
					letter.setOnLongClickListener { _ ->
						showColorPickerDialog(this, key)
						true
					}
					letter.text = key.letter
					letter.setBackgroundResource(key.state.background)
					enteredWord.append(letter.text.toString())
					key.addListener(letter)
				}
			}

			key.view.setOnLongClickListener {
				showColorPickerDialog(this, key)
				true
			}
		}


		keyBackspace.setOnClickListener {
			tapSound()
			vibrate()
			if (enteredWord.isNotEmpty()) {
				val ix = enteredWord.length - 1
				deleteEnteredCharAtIx(ix)
				enteredWord.deleteCharAt(ix)
			}
		}

		keyBackspace.setOnLongClickListener {
			penClickSound()
			for (ix in 0 until enteredWord.length) {
				deleteEnteredCharAtIx(ix)
			}
			enteredWord = StringBuilder()
			true
		}

		keySubmit.setOnClickListener {
			if (enteredWord.length == wordLength) {
				if (validWords.contains(enteredWord.toString().toUpperCase())) {
					numGuesses++
					if (enteredWord.toString() == targetWord) {
						refillUserInputFieldWithTiles()
						showGameOverDialog(this, true)
					} else {
						val guessView = layoutInflater.inflate(R.layout.guess_item, null)
						val guessedWord = guessView.findViewById<LinearLayout>(R.id.layoutGuessedWord)
						guessView.findViewById<TextView>(R.id.textViewMatchCount).text = compareWord(enteredWord.toString()).toString()
						moveWordToView(guessedWord)
						layoutGuessedWords.addView(guessView, 0)
						enteredWord = StringBuilder()
						refillUserInputFieldWithTiles()
						textViewGuessCount.text = numGuesses.toString()
					}
				} else {
					layoutCorrectWord.clearAnimation()
					layoutCorrectWord.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
				}
			}
		}

		mTimer = object : Runnable {
			override fun run() {
				numSeconds++
				textViewTimer.text = secondsToTimeDisplay(numSeconds)
				if (mIsRunning) mHandler.postDelayed(this, 1000)
			}
		}

		buttonPause.setOnClickListener {
			if (mIsRunning) pause() else play()
		}

		keyBackspace.setOnTouchListener(ScaleOnTouch)
		keySubmit.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)
		buttonPause.setOnTouchListener(ScaleOnTouch + PenClickOnTouch)

//		adView.loadAd(AdRequest.Builder().build())
	}

	private fun addValidWords(vararg ids: Int) {
		ids.forEach { id ->
			BufferedReader(InputStreamReader(resources.openRawResource(id))).forEachLine { line ->
				validWords.add(line.split("\t")[0].toUpperCase())
			}
		}
	}

	fun getOdds(): Int {
		return validWords.size
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		play()
	}

	override fun onPause() {
		super.onPause()
		pause()
	}

	private fun deleteEnteredCharAtIx(ix: Int) {
		val tile = layoutCorrectWord.getChildAt(ix) as TextView
		keys[tile.text]!!.removeListener(tile)
		tile.text = ""
		tile.setBackgroundResource(KeyState.BLANK.background)
		tile.setOnLongClickListener(null)
	}

	override fun onBackPressed() {
		pause()
	}

	private fun refillUserInputFieldWithTiles() {
		layoutCorrectWord.removeAllViews()
		for (i in 0 until wordLength) {
			val tile = newBlankTile()
			layoutCorrectWord.addView(tile)
			animatePopIn(tile)
		}
	}

	private fun moveWordToView(to: LinearLayout) {
		for (i in layoutCorrectWord.childCount - 1 downTo 0) {
			val letterView = layoutCorrectWord.getChildAt(i) as TextView?
			layoutCorrectWord.removeView(letterView)
			to.addView(letterView, 0)
			animatePopIn(letterView as View)
		}
	}

	fun newBlankTile(): TextView {
		val tile = TextView(this)
		tile.setTextColor(ContextCompat.getColor(this, android.R.color.white))
		tile.typeface = ResourcesCompat.getFont(this, R.font.architects_daughter)
		tile.textSize = 34f
		tile.setBackgroundResource(KeyState.BLANK.background)
		val size = dpToPx(40)
		tile.gravity = Gravity.CENTER
		val params = ConstraintLayout.LayoutParams(size, size)
		params.setMargins(2, 2, 2, 2)
		tile.layoutParams = params
		if (isSdkAtLeastLollipop()) tile.elevation = dpToPx(2).toFloat()

		return tile
	}

	private fun compareWord(guess: String): Int {
		var matchCount = 0
		var wordCopy = targetWord
		guess.toCharArray().forEach { letter ->
			if (wordCopy.contains(letter)) {
				matchCount++
				wordCopy = wordCopy.replace(letter.toString(), "")
			}
		}
		return matchCount
	}

	@SuppressLint("InflateParams")
	fun pause() {
		if (mIsRunning) {
			buttonPause.setImageResource(android.R.drawable.ic_media_play)
			mIsRunning = false
			mHandler.removeCallbacks(mTimer)
			if (!isGameOver) showPauseDialog(this, keys)
		}
	}

	fun play() {
		if (!mIsRunning && !isGameOver) {
			buttonPause.setImageResource(android.R.drawable.ic_media_pause)
			mIsRunning = true
			mHandler.postDelayed(mTimer, 1000)
		}
	}

}