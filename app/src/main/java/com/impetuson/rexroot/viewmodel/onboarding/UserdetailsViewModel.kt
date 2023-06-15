package com.impetuson.rexroot.viewmodel.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

class UserdetailsViewModel: ViewModel() {

    val months = mapOf(1 to "Jan",2 to "Feb",3 to "Mar",4 to "Apr",5 to "May",6 to "Jun",7 to "Jul",8 to "Aug",9 to "Sep",10 to "Oct",11 to "Nov",12 to "Dec")

    private var auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private val calendar = Calendar.getInstance()

    private val _dobDay = MutableLiveData<Int>(0)
    var dobDay: LiveData<Int> = _dobDay

    private val _dobMonth = MutableLiveData<Int>(0)
    var dobMonth: LiveData<Int> = _dobMonth

    private val _dobYear = MutableLiveData<Int>(0)
    var dobYear: LiveData<Int> = _dobYear

    private val _dobUser = MutableLiveData<String>("")
    val dobUser: LiveData<String> = _dobUser

    fun setdobDay(day: Int){ _dobDay.value = day }
    fun setdobMonth(month: Int){ _dobMonth.value = month }
    fun setdobYear(year: Int){ _dobYear.value = year }

    fun currentDate(): List<Int> {
        return listOf(calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR))
    }

    fun setDob() {
        _dobUser.value = "${dobDay.value}-${months[dobMonth.value]}-${dobYear.value}"
        Log.d("Date","${_dobUser.value}")
        Log.d("Date","${dobUser.value}")
    }
    fun calcAge(): Int{
        return calendar.get(Calendar.YEAR).minus(dobYear.value ?: 0)
    }

    suspend fun storeDataToFirestore(): List<Any> = withContext(Dispatchers.IO){
        var storeMsg: String = ""
        var storeStatus: Boolean = false

        val userId = auth.currentUser!!.uid
        val age = calcAge()

        val data = mapOf<String,Any>(
            "profiledata.age" to age,
            "profiledata.dob" to "${dobUser.value}"
        )

        try{
            db.collection("users").document(userId).update(data).await()
            Log.d("FirestoreDB","Document Reference added: $userId")
            storeStatus = true
            storeMsg = "User details added successfully"
        } catch (e: Exception) {
            storeMsg = "Error occurred"
        }

        listOf(storeStatus,storeMsg)
    }
}