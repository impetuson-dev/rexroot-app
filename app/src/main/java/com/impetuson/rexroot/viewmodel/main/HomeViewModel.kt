package com.impetuson.rexroot.viewmodel.main

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeViewModel: ViewModel() {

    private val _userName = MutableLiveData<String>("")
    var userName: LiveData<String> = _userName

    private val _recentJobsId = MutableLiveData<List<String>>()
    var recentJobsId: LiveData<List<String>> = _recentJobsId

    fun getUserProfileDetails(sharedPreference: SharedPreferences){
        _userName.value = sharedPreference.getString("username","")
    }

    fun fetchRecentJobs(sharedPreference: SharedPreferences){
        val gson = Gson()
        val json = sharedPreference.getString("recent_list", null)

        val stringList: List<String> = gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        Log.d("recent_list", stringList.toString())
        _recentJobsId.value = stringList
    }

}