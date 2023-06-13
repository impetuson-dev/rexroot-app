package com.impetuson.rexroot.view.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.R
import com.impetuson.rexroot.viewmodel.onboarding.SplashViewModel


@SuppressLint("CustomSplashScreen")
class SplashscreenFragment : Fragment() {

    private val viewmodel: SplashViewModel by activityViewModels()
    private val SPLASH_TIME: Long = 1500

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
            if (viewmodel.userAlreadySignedIn()){ findNavController().navigate(R.id.action_splashscreenFragment_to_homeFragment) }
            else { findNavController().navigate(R.id.action_splashscreenFragment_to_loginFragment) } }
            , SPLASH_TIME)

    }
}