package com.impetuson.rexroot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.impetuson.rexroot.model.LoginModelClass

class LoginViewModel: ViewModel() {

    private val _loginModel = MutableLiveData<LoginModelClass>(LoginModelClass("",""))
    val loginModel: LiveData<LoginModelClass> = _loginModel

    private val _userEmailError = MutableLiveData<String?>("")
    val userEmailError: LiveData<String?> = _userEmailError

    private val _userPasswordError = MutableLiveData<String?>("")
    val userPasswordError: LiveData<String?> = _userPasswordError

    fun loginFormValidation(): Boolean{
        val email = loginModel.value?.userEmail ?: ""
        val password = loginModel.value?.userPassword ?: ""
        var emailValidation = false
        var passwordValidation = false

        if (email.isEmpty()){ _userEmailError.value = "Email address cannot be empty" }
        else if (!isEmailValid(email)){ _userEmailError.value = "Valid Email address is required" }
        else {
            _userEmailError.value = null
            emailValidation = true
        }

        if (password.isEmpty()){ _userPasswordError.value="Password cannot be empty" }
        else {
            _userPasswordError.value = null
            passwordValidation = true
        }

        return emailValidation && passwordValidation
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun resetViewModel(){
        _userEmailError.value = null
        _userPasswordError.value = null
    }

}