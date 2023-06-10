package com.impetuson.rexroot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.impetuson.rexroot.model.LoginModelClass
import com.impetuson.rexroot.model.SignupModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignupViewModel: ViewModel() {

    private lateinit var auth: FirebaseAuth

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

    suspend fun signupAuthentication(): List<Any> = withContext(Dispatchers.IO){
        auth = Firebase.auth
        var authMsg: String = ""
        var authStatus: Boolean = false
        val email = signupModel.value?.userEmail ?: ""
        val password = signupModel.value?.userPassword ?: ""

        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Log.d("FirebaseAuth", "Sign up success")
            authMsg = "Sign up successful"
            authStatus = true
            val user = auth.currentUser
        } catch (e: Exception) {
            Log.d("FirebaseAuth", "Sign up failed", e)
            if (e is FirebaseAuthUserCollisionException) {
                authMsg = "Account already exists"
            } else {
                authMsg = "Sign up failed"
            }
        }

        listOf(authStatus, authMsg)
    }

    fun resetViewModel(){
        _userFullNameError.value = null
        _userEmailError.value = null
        _userMobileNumberError.value = null
        _userPasswordError.value = null
    }

}