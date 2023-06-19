package com.impetuson.rexroot.viewmodel.onboarding

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.impetuson.rexroot.model.onboarding.SignupModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignupViewModel: ViewModel() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    private var fullname: String = ""
    private var email: String = ""
    private var mobilenumber: String = ""
    private var userId: String = ""

    private val _signupModel = MutableLiveData<SignupModelClass>(SignupModelClass("","","","",0))
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

    suspend fun storeDataToFirestore(): List<Any> = withContext(Dispatchers.IO){
        var storeMsg: String = ""
        var storeStatus: Boolean = false

        fullname = signupModel.value?.userFullName ?: ""
        email = signupModel.value?.userEmail ?: ""
        mobilenumber = signupModel.value?.userMobileNumber ?: ""
        userId = auth.currentUser!!.uid

        val userdata = hashMapOf(
            "profiledata" to hashMapOf(
                "userid" to userId,
                "fullname" to fullname.trim(),
                "email" to email.trim(),
                "mobilenumber" to mobilenumber.trim(),
            ),
            "submitdata" to hashMapOf()
        )

        try{
            db.collection("users").document(userId).set(userdata).await()
            Log.d("FirestoreDB","Document Reference added: $userId")
            storeStatus = true
            storeMsg = "User details added successfully"
        } catch (e: Exception) {
            storeMsg = "Error occurred"
        }

        listOf(storeStatus,storeMsg)
    }

    fun resetViewModel(){
        _userFullNameError.value = null
        _userEmailError.value = null
        _userMobileNumberError.value = null
        _userPasswordError.value = null
    }

    fun storeDataToSharedPreferences(sharedPreferences: SharedPreferences){
        val editor = sharedPreferences.edit()
        editor.putString("userid",userId)
        editor.putString("useremail",email)
        editor.putString("username",fullname)
        editor.putString("usermobilenumber",mobilenumber)
        editor.apply()
    }

}