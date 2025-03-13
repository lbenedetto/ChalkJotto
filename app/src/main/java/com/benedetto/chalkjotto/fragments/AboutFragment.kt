package com.benedetto.chalkjotto.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.benedetto.chalkjotto.BuildConfig
import com.benedetto.chalkjotto.MainActivity
import com.benedetto.chalkjotto.R
import com.benedetto.chalkjotto.TitleTag
import com.benedetto.chalkjotto.databinding.FragmentAboutBinding
import com.benedetto.chalkjotto.definitions.ScaleOnTouch
import com.benedetto.chalkjotto.definitions.Sound

class AboutFragment : Fragment() {
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAboutBinding.inflate(layoutInflater, container, false)

        binding.tvContactMe.setOnClickListener {
            val sendEmailTo = "chalkjotto@gmail.com"

            val clipboard = ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)
            clipboard?.setPrimaryClip(ClipData.newPlainText(sendEmailTo, sendEmailTo))

            Toast.makeText(requireContext(), "Copied $sendEmailTo", Toast.LENGTH_SHORT)
                .show()
        }

        binding.buttonViewCode.setOnClickListener {
            Sound.tapSound()
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = "https://github.com/lbenedetto/ChalkJotto/issues".toUri()
            })
        }
        binding.buttonViewCode.setOnTouchListener(ScaleOnTouch)

        binding.buttonViewPrivacyPolicy.setOnClickListener {
            Sound.tapSound()
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = "https://larsbenedetto.work/ChalkJotto/PrivacyPolicy.html".toUri()
            })
        }
        binding.buttonViewPrivacyPolicy.setOnTouchListener(ScaleOnTouch)

        binding.textViewAppVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        binding.buttonDone.setOnClickListener {
            Sound.tapSound()
            (requireActivity() as MainActivity).goToFragment(TitleTag)
        }
        binding.buttonDone.setOnTouchListener(ScaleOnTouch)

        return binding.root
    }
}
