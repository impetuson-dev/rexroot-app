package com.impetuson.rexroot.viewmodel.onboarding

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashViewModel:ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth

    fun userAlreadySignedIn(): Boolean{
        return auth.currentUser != null
    }
}