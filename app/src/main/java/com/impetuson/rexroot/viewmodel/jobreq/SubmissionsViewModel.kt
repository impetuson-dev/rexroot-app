package com.impetuson.rexroot.viewmodel.jobreq

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.impetuson.rexroot.JobreqActivity
import com.impetuson.rexroot.model.jobreq.SubmissionsModelClass
import kotlinx.coroutines.tasks.await

class SubmissionsViewModel(private var jobId: String): ViewModel() {

    private val firestoreDB = Firebase.firestore
    private var userId = ""

    suspend fun fetchDataFromFirestore(): List<SubmissionsModelClass> {
        val resumeNames = mutableListOf<SubmissionsModelClass>()
        Log.d("firestoredb","fetching ... userid: $userId | jobid: $jobId")

        val documentSnapshot = firestoreDB.collection("users").document(userId).get().await()
        var submissionsModel: SubmissionsModelClass

        if (documentSnapshot != null){
            val submitData = documentSnapshot.get("submitdata") as? Map<String,Any>
            Log.d("submitdata",submitData.toString())
            var submissions = submitData?.get(jobId) as? Map<String,Any>
            submissions = submissions?.toSortedMap()
            submissions?.forEach { t, u ->
                u as Map<String, Any>
                if (u["resumename"] != null){
                    submissionsModel = SubmissionsModelClass(
                        resumename = u["resumename"].toString(),
                        resumepost = u["resumepost"].toString()
                    )
                    resumeNames.add(0,submissionsModel)
                }
            }
        }

        Log.d("resumenames",resumeNames.toString())
        return resumeNames
    }

    fun fetchDataSharedPref(sharedPreferences: SharedPreferences){
        userId = sharedPreferences.getString("userid","").toString()
    }

}