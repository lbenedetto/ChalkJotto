package com.benedetto.chalkjotto.dialogs

import com.benedetto.chalkjotto.AcceptChallengeTag
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.databinding.DialogLessonBinding
import com.benedetto.chalkjotto.definitions.DataManager
import com.benedetto.chalkjotto.definitions.Sound
import com.benedetto.chalkjotto.fragments.Lesson

fun showLessonDialog(lesson: Lesson, activity: MainActivity) {
    val binding = DialogLessonBinding.inflate(activity.layoutInflater)
    val popupWindow = PopupDialog(activity, binding.root)

    binding.tvLesson.text = activity.getString(lesson.fullText)

    binding.buttonContinue.setOnClickListener {
        Sound.tapSound()
        DataManager.activeLesson = lesson.id
        activity.intent.putExtra("payload", lesson.payload)
        activity.goToFragment(AcceptChallengeTag)
        popupWindow.popup.dismiss()
    }

    popupWindow.show()
}