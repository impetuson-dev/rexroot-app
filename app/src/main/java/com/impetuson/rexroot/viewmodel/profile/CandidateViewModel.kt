package com.impetuson.rexroot.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.impetuson.rexroot.model.profile.CandidateModelClass

class CandidateViewModel: ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _candidateModel = MutableLiveData<CandidateModelClass>(CandidateModelClass("","","","","","","","",""))
    val candidateModel: LiveData<CandidateModelClass> = _candidateModel

    fun saveDetails(){
        val candidate = CandidateModelClass(
            candidateFullName = candidateModel.value?.candidateFullName ?: "",
            candidateEmail = candidateModel.value?.candidateEmail ?: "",
            candidateMobileNumber = candidateModel.value?.candidateMobileNumber ?: "",
            candidateGender = candidateModel.value?.candidateGender ?: "",
            candidateCurrLocation = candidateModel.value?.candidateCurrLocation ?: "",
            candidateDesLocation = candidateModel.value?.candidateDesLocation ?: "",
            candidateWillToRelocate = candidateModel.value?.candidateWillToRelocate ?: "",
            candidateCoreSkills = candidateModel.value?.candidateCoreSkills ?: "",
            candidateSuppSkills = candidateModel.value?.candidateSuppSkills ?: "",
        )
        Log.d("candidate","${candidate}")
        storeDataToFirestore(candidate)
    }

    private fun storeDataToFirestore(candidate: CandidateModelClass) {
        val userId = auth.currentUser!!.uid
        var storeMsg: String = ""
        var storeStatus: Boolean = false

        val candidatedata = hashMapOf(
            "candidatedata" to objectToMap(candidate)
        )

        Log.d("candidatedata","${candidatedata}")


        try{
            db.collection("users").document(userId).set(candidatedata, SetOptions.merge())
            Log.d("FirestoreDB","candidate Data added: $userId")
            storeStatus = true
            storeMsg = "candidate details updated successfully"
        } catch (e: Exception) {
            storeMsg = "Error occurred"
        }

        listOf(storeStatus,storeMsg)
    }

    fun objectToMap(obj: Any): Map<String, Any?> {
        val gson = Gson()
        val json = gson.toJsonTree(obj)
        return gson.fromJson(json, Map::class.java) as Map<String, Any?>
    }

}