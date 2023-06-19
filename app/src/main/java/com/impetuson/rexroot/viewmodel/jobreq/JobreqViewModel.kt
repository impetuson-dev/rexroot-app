package com.impetuson.rexroot.viewmodel.jobreq

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.impetuson.rexroot.model.jobreq.JobReqModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JobreqViewModel: ViewModel() {

    private lateinit var realtimeDB: DatabaseReference

    private val _jobreqModel = MutableLiveData<JobReqModelClass>()
    var jobreqModel: LiveData<JobReqModelClass> = _jobreqModel

    private var userId: String = ""
    var jobId: String = ""

    suspend fun fetchRealtimeDB() = withContext(Dispatchers.IO){
        realtimeDB = Firebase.database.reference
        Log.d("Firebase RealtimeDB","Jobid: ${jobId}")

        try {
            realtimeDB.child("jobreq").child(jobId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()){
                    val value = dataSnapshot.value
                    val jobDataMap = value as Map<String, Any>
                    val jobData = convertMapToObject(jobDataMap)
                    _jobreqModel.value = jobData

                    Log.d("Firebase RealtimeDB","$jobDataMap")
                }
            }.await()

            Log.d("Firebase RealtimeDB","Fetched data successfully")
        } catch (e: Exception) {
            Log.d("Firebase RealtimeDB","Error: ${e.message}")
        }
    }

    fun fetchDataSharedPref(sharedPreferences: SharedPreferences){
        userId = sharedPreferences.getString("userid","").toString()
    }

    private fun convertMapToObject(jobDataMap: Map<String,Any>): JobReqModelClass{
        val gson = Gson()
        val jsonString = gson.toJson(jobDataMap)
        return gson.fromJson(jsonString, object : TypeToken<JobReqModelClass>() {}.type)
    }

    fun calcJobPostedTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return currentDateTime.format(formatter).toString()
    }

}