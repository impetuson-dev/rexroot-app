package com.impetuson.rexroot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.impetuson.rexroot.view.LoginFragment

class ValidationViewModel: ViewModel() {

    private val _userEmail = MutableLiveData<String>("")
    val userEmail: LiveData<String> = _userEmail

    private val _userEmailError = MutableLiveData<String?>("")
    val userEmailError: LiveData<String?> = _userEmailError

    private val _userMobileNumber = MutableLiveData<String>("")
    val userMobileNumber: LiveData<String> = _userMobileNumber

    private val _userMobileError = MutableLiveData<String?>("")
    val userMobileError: LiveData<String?> = _userMobileError

    private val _userPassword = MutableLiveData<String>("")
    val userPassword: LiveData<String> = _userPassword

    private val _userPasswordError = MutableLiveData<String?>("")
    val userPasswordError: LiveData<String?> = _userPasswordError

    fun setUserEmail(userEmail: String){ _userEmail.value = userEmail }
    fun setUserMobileNumber(userMobileNumber: String){ _userMobileNumber.value = userMobileNumber }
    fun setUserPassword(userPassword: String){ _userPassword.value = userPassword }

    fun formValidation(){
        val email = userEmail.value ?: ""
        val password = userPassword.value ?: ""

        if (email.isEmpty()){ _userEmailError.value = "Email address cannot be empty" }
        else { _userEmailError.value = null }

        if (password.isEmpty()){ _userPasswordError.value="Password cannot be empty" }
        else { _userPasswordError.value = null }
    }

}