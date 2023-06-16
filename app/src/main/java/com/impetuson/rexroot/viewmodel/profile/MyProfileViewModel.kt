package com.impetuson.rexroot.viewmodel.profile

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyProfileViewModel: ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private var _userName = MutableLiveData<String>("")
    val userName: LiveData<String> = _userName

    private var _userEmailID = MutableLiveData<String>("")
    val userEmailID: LiveData<String> = _userEmailID

    private var _userMobileNumber = MutableLiveData<String>("")
    val userMobileNumber: LiveData<String> = _userMobileNumber

    fun getUserProfileDetails(sharedPreference: SharedPreferences){
        _userName.value = sharedPreference.getString("username","")
        _userEmailID.value = sharedPreference.getString("useremail","")
        _userMobileNumber.value = sharedPreference.getString("usermobilenumber","")

        Log.d("sharedpreference",sharedPreference.getString("username","").toString())
    }

    fun btnLogOutHandler(){
        auth.signOut()
    }

}