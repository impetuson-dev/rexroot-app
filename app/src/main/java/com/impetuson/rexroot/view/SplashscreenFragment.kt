package com.impetuson.rexroot.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.R

/**
 * First screen of the app: Splash Screen
 */
const val SPLASH_TIME: Long = 1500

class SplashscreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splashscreen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
                findNavController().navigate(R.id.action_splashscreenFragment_to_loginFragment)
            }, SPLASH_TIME)

    }
}