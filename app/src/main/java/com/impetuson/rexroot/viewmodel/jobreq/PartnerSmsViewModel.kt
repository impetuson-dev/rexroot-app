package com.impetuson.rexroot.viewmodel.jobreq

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PartnerSmsViewModel : ViewModel() {

    private val _partners_name = MutableLiveData<String>("")
    val partners_name: LiveData<String> get() = _partners_name

    private val _mobile_no = MutableLiveData<String>("")
    val mobile_no: LiveData<String> get() = _mobile_no

    private val _choice = MutableLiveData<Boolean>(true)
    val choice: LiveData<Boolean> get() = _choice


    fun update_choice(choice : Boolean){
        _choice.value = choice
    }

    fun update_mobile_no(mob_no : String,name: String){
        _mobile_no.value = mob_no
        _partners_name.value = name
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("JobreqActivity", "PartnerSmsViewModel destroyed!")
    }
}