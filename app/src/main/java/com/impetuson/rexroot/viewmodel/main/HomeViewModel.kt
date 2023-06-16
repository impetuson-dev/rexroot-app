package com.impetuson.rexroot.viewmodel.main

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {

    private val _userName = MutableLiveData<String>("")
    var userName: LiveData<String> = _userName

    fun getUserProfileDetails(sharedPreference: SharedPreferences){
        _userName.value = sharedPreference.getString("username","")
    }

}