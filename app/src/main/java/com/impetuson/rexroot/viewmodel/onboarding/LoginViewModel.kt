package com.impetuson.rexroot.viewmodel.onboarding

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.impetuson.rexroot.model.onboarding.LoginModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel: ViewModel() {

    private var userId: String = ""
    private var auth: FirebaseAuth = Firebase.auth
    private var firestoreRef = FirebaseFirestore.getInstance().collection("users")
    private var fetchedData: Map<String,Any>? = null

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
            userId = auth.currentUser?.uid.toString()
            Log.d("FirebaseAuth", "Login success")
            authMsg = "Login successful"
            authStatus = true

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

    suspend fun fetchUserDataFromFirestore(): Boolean = withContext(Dispatchers.IO){
        var fetchStatus: Boolean = false

        try{
            val task = firestoreRef.document("$userId").get().await()
            val document = task.data?.get("profiledata")
            Log.d("Firestore", document.toString())
            Log.d("Firebase Firestore","User data fetched successfully")
            fetchStatus = true
            fetchedData = document as Map<String, Any>
        } catch (e: Exception){
            Log.d("Firebase Firestore", "User data fetch unsuccessful")
        }

        fetchStatus
    }

    fun storeDataToSharedPreferences(sharedPreferences: SharedPreferences){
        val editor = sharedPreferences.edit()
        if (fetchedData.isNullOrEmpty()){
            Log.d("Firebase Firestore","Fetched data is null")
        }
        editor.putString("userid",userId)
        editor.putString("useremail", fetchedData?.get("email").toString())
        editor.putString("username", fetchedData?.get("fullname").toString())
        editor.putString("usermobilenumber", fetchedData?.get("mobilenumber").toString())
        editor.apply()
    }
}