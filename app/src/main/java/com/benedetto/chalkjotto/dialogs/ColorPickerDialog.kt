package com.benedetto.chalkjotto.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.benedetto.chalkjotto.databinding.KeystateMenuBinding
import com.benedetto.chalkjotto.definitions.Key
import com.benedetto.chalkjotto.definitions.KeyState
import com.benedetto.chalkjotto.definitions.Sound.tapSound
import com.benedetto.chalkjotto.game.GameModel


@SuppressLint("InflateParams")
fun showColorPickerDialog(context: Context, key: Key, model: GameModel) {
    val dialog = Dialog(context)
    val inflater = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
    val binding = KeystateMenuBinding.inflate(inflater, null, false)

    binding.keyWhite.text = key.letter
    binding.keyGreen.text = key.letter
    binding.keyRed.text = key.letter
    binding.keyYellow.text = key.letter
    binding.keyBlue.text = key.letter
    binding.keyPink.text = key.letter

    binding.keyWhite.setOnClickListener {
        tapSound()
        model.updateState(key, KeyState.BLANK)
        dialog.dismiss()
    }
    binding.keyGreen.setOnClickListener {
        tapSound()
        model.updateState(key, KeyState.YES)
        dialog.dismiss()
    }
    binding.keyRed.setOnClickListener {
        tapSound()
        model.updateState(key, KeyState.NO)
        dialog.dismiss()
    }
    binding.keyYellow.setOnClickListener {
        tapSound()
        model.updateState(key, KeyState.MAYBE)
        dialog.dismiss()
    }
    binding.keyBlue.setOnClickListener {
        tapSound()
        model.updateState(key, KeyState.MAYBE_BLUE)
        dialog.dismiss()
    }
    binding.keyPink.setOnClickListener {
        tapSound()
        model.updateState(key, KeyState.MAYBE_PINK)
        dialog.dismiss()
    }

    dialog.setContentView(binding.root)
    val window = dialog.window!!
    window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    window.setBackgroundDrawableResource(android.R.color.transparent)
    window.setGravity(Gravity.CENTER or Gravity.BOTTOM)
    dialog.show()
}