package com.impetuson.rexroot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.impetuson.rexroot.model.LoginModelClass
import com.impetuson.rexroot.model.SignupModelClass

class SignupViewModel: ViewModel() {

    private val _signupModel = MutableLiveData<SignupModelClass>(SignupModelClass("","","",""))
    val signupModel: LiveData<SignupModelClass> = _signupModel

    private val _userFullNameError = MutableLiveData<String?>("")
    val userFullNameError: LiveData<String?> = _userFullNameError

    private val _userEmailError = MutableLiveData<String?>("")
    val userEmailError: LiveData<String?> = _userEmailError

    private val _userMobileNumberError = MutableLiveData<String?>("")
    val userMobileNumberError: LiveData<String?> = _userMobileNumberError

    private val _userPasswordError = MutableLiveData<String?>("")
    val userPasswordError: LiveData<String?> = _userPasswordError

    fun signupFormValidation(): Boolean{
        val fullname = signupModel.value?.userFullName ?: ""
        val email = signupModel.value?.userEmail ?: ""
        val mobilenumber = signupModel.value?.userMobileNumber ?: ""
        val password = signupModel.value?.userPassword ?: ""
        var fullnameValidation = false
        var emailValidation = false
        var mobilenumberValidation = false
        var passwordValidation = false

        if (fullname.isEmpty()){ _userFullNameError.value = "Full name cannot be empty" }
        else {
            _userFullNameError.value = null
            fullnameValidation = true
        }

        if (email.isEmpty()){ _userEmailError.value = "Email address cannot be empty" }
        else if (!isEmailValid(email)){ _userEmailError.value = "Valid Email address is required" }
        else {
            _userEmailError.value = null
            emailValidation = true
        }

        if (mobilenumber.isEmpty()){
            _userMobileNumberError.value = "Mobile number cannot be empty"
        }
        else if (!isMobileNumberValid(mobilenumber)) { _userMobileNumberError.value = "Valid Mobile number is required" }
        else {
            _userMobileNumberError.value = null
            mobilenumberValidation = true
        }

        if (password.isEmpty()){ _userPasswordError.value="Password cannot be empty" }
        else {
            _userPasswordError.value = null
            passwordValidation = true
        }

        return fullnameValidation && emailValidation && mobilenumberValidation && passwordValidation
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isMobileNumberValid(mobilenumber: String): Boolean {
        if (mobilenumber.length < 10) return false
        else return mobilenumber.all { it.isLetterOrDigit() }
    }

    fun resetViewModel(){
        _userFullNameError.value = null
        _userEmailError.value = null
        _userMobileNumberError.value = null
        _userPasswordError.value = null
    }

}