package com.impetuson.rexroot.viewmodel.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.impetuson.rexroot.model.onboarding.LoginModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel: ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth

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

    suspend fun loginAuthentication(): List<Any> = withContext(Dispatchers.IO){
        var authMsg: String = ""
        var authStatus: Boolean = false
        val email = loginModel.value?.userEmail ?: ""
        val password = loginModel.value?.userPassword ?: ""

        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d("FirebaseAuth", "Login success")
            authMsg = "Login successful"
            authStatus = true
            val user = auth.currentUser
        } catch (e: Exception) {
            Log.d("FirebaseAuth", "Login failed", e)
            if (e is FirebaseAuthInvalidUserException) {
                authMsg = "Account does not exist"
            } else {
                authMsg = "Invalid Email or Password"
            }
        }

        listOf(authStatus,authMsg)
    }
}