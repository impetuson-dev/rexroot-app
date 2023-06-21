package com.impetuson.rexroot.viewmodel.jobreq

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.impetuson.rexroot.JobreqActivity
import com.impetuson.rexroot.model.jobreq.SubmissionsModelClass
import kotlinx.coroutines.tasks.await

class SubmissionsViewModel(private var jobId: String): ViewModel() {

    private val firestoreDB = Firebase.firestore
    private var userId = ""

    val items = listOf("Active", "Select", "Reject")

    private val _selectedItem = MutableLiveData<String>()
    var selectedItem: LiveData<String> = _selectedItem

    fun onItemSelected(position: Int) {
        _selectedItem.value = items[position].trim()
    }

    fun setFilteredList(){
        when (selectedItem.value){

        }
    }

    suspend fun fetchDataFromFirestore(): List<List<SubmissionsModelClass>> {
        val activeList = mutableListOf<SubmissionsModelClass>()
        val selectList = mutableListOf<SubmissionsModelClass>()
        val rejectList = mutableListOf<SubmissionsModelClass>()

        Log.d("firestoredb","fetching ... userid: $userId | jobid: $jobId")

        val documentSnapshot = firestoreDB.collection("users").document(userId).get().await()
        var submissionsModel: SubmissionsModelClass

        if (documentSnapshot != null){
            val submitData = documentSnapshot.get("submitdata") as? Map<String,Any>
            Log.d("submitdata",submitData.toString())
            var submissions = submitData?.get(jobId) as? Map<String,Any>
            submissions = submissions?.get("resume") as? Map<String, Any>
            submissions = submissions?.toSortedMap()

            submissions?.forEach { t, u ->
                u as Map<String, Any>

                submissionsModel = SubmissionsModelClass(
                    resumename = u["resumename"].toString(),
                    resumepost = u["resumepost"].toString(),
                    resumestatus = u["resumestatus"].toString()
                )

                when (u["resumestatus"].toString()){
                    "0" -> activeList.add(0, submissionsModel)
                    "1" -> selectList.add(0, submissionsModel)
                    "-1" -> rejectList.add(0, submissionsModel)
                    else -> activeList.add(0, submissionsModel)
                }

            }
        }

        return listOf(activeList,selectList,rejectList)
    }

    fun fetchDataSharedPref(sharedPreferences: SharedPreferences){
        userId = sharedPreferences.getString("userid","").toString()
    }

}