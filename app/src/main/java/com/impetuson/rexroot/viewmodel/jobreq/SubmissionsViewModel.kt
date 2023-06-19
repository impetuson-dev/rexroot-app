package com.impetuson.rexroot.viewmodel.jobreq

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.impetuson.rexroot.JobreqActivity
import kotlinx.coroutines.tasks.await

class SubmissionsViewModel: ViewModel() {

    private val firestoreDB = Firebase.firestore
    private var userId = ""
    private val jobId = "-NY32jt-jEWFPPTQGqZ9"

    suspend fun fetchDataFromFirestore(): List<String> {
        val resumeNames = mutableListOf<String>()
        Log.d("firestoredb","fetching ... userid: $userId")

        val documentSnapshot = firestoreDB.collection("users").document(userId).get().await()

        if (documentSnapshot != null){
            val submitData = documentSnapshot.get("submitdata") as? Map<String,Any>
            Log.d("submitdata",submitData.toString())
            val submissions = submitData?.get(jobId) as? Map<String,Any>
            submissions?.forEach { t, u ->
                u as Map<String, Any>
                if (u["resumename"] != null){ resumeNames.add(u["resumename"].toString()) }
            }
        }

        Log.d("resumenames",resumeNames.toString())
        return resumeNames
    }

    fun fetchDataSharedPref(sharedPreferences: SharedPreferences){
        userId = sharedPreferences.getString("userid","").toString()
    }

}