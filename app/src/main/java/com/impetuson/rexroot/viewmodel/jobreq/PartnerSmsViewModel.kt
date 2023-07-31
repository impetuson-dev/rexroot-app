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

    private val _otp_no = MutableLiveData<String>("")
    val otp_no: LiveData<String> get() = _otp_no

    private val _choice = MutableLiveData<Boolean>(true)
    val choice: LiveData<Boolean> get() = _choice

    private val _send_button_status = MutableLiveData<Boolean>(true)
    val send_button_status: LiveData<Boolean> get() = _send_button_status

    private val _verify_button_status = MutableLiveData<Boolean>(false)
    val verify_button_status: LiveData<Boolean> get() = _verify_button_status

    private val _verify_status = MutableLiveData<String>("Verify")
    val verify_status: LiveData<String> get() = _verify_status

    fun update_choice(choice : Boolean){
        _choice.value = choice
    }

    fun update_mobile_no(mob_no : String,name: String){
        _mobile_no.value = mob_no
        _partners_name.value = name
    }

    fun update_otp_no(ot_no : String){
        _otp_no.value = ot_no
    }

    fun update_send_button(but : Boolean){
        _send_button_status.value = but
    }

    fun update_verify_button(but: Boolean){
        _verify_button_status.value = but
    }

    fun update_verify_status(status: String){
        _verify_status.value = status
    }
    override fun onCleared() {
        super.onCleared()
        Log.d("JobreqActivity", "PartnerSmsViewModel destroyed!")
    }
}