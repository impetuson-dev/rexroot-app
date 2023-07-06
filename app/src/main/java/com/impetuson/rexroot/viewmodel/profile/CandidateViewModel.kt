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
import com.impetuson.rexroot.model.profile.PartnerModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CandidateViewModel: ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    val _userId = MutableLiveData<String>("")

    private val _candidateModel = MutableLiveData(CandidateModelClass())
    val candidateModel: LiveData<CandidateModelClass> = _candidateModel

    fun setDetails(candidate: CandidateModelClass) {
        _candidateModel.value = candidate
    }


    fun saveDetails(){
        var candidate = CandidateModelClass(
            candidateFullName = candidateModel.value?.candidateFullName ?: "",
            candidateEmail = candidateModel.value?.candidateEmail ?: "",
            candidateMobileNumber = candidateModel.value?.candidateMobileNumber ?: "",
            candidateGender = candidateModel.value?.candidateGender ?: "",
            candidateCurrLocation = candidateModel.value?.candidateCurrLocation ?: "",
            candidateDesLocation = candidateModel.value?.candidateDesLocation ?: "",
            candidateWillToRelocate = candidateModel.value?.candidateWillToRelocate ?: 0,
            candidateCoreSkills = candidateModel.value?.candidateCoreSkills ?: "",
            candidateSuppSkills = candidateModel.value?.candidateSuppSkills ?: "",
        )

        Log.d("candidate","$candidate")
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

    private fun objectToMap(obj: Any): Map<String, Any?> {
        val gson = Gson()
        val json = gson.toJsonTree(obj)
        return gson.fromJson(json, Map::class.java) as Map<String, Any?>
    }

    suspend fun fetchDataFromFirestore(): List<Any> = withContext(Dispatchers.IO) {

        var candidate = CandidateModelClass()

        val documentSnapshot = db.collection("users").document(_userId.value.toString()).get()
            .addOnFailureListener {  e ->
                Log.d("Firebase Firestore","ERROR: ${e.message}")
            }
            .await()

        if (documentSnapshot != null){
            val submitData = documentSnapshot.get("candidatedata") as? Map<String,Any>

            Log.d("Candidate profile", "Candidate Data: ${submitData.toString()}")

            if (submitData.isNullOrEmpty()) {
                return@withContext listOf(false, candidate)
            }

            Log.d("partnerdata",submitData.toString())

            candidate = CandidateModelClass(
                candidateFullName = submitData["candidateFullName"].toString(),
                candidateEmail = submitData["candidateEmail"].toString(),
                candidateMobileNumber = submitData["candidateMobileNumber"].toString(),
                candidateGender = submitData["candidateGender"].toString(),
                candidateCurrLocation = submitData["candidateCurrLocation"].toString(),
                candidateDesLocation = submitData["candidateDesLocation"].toString(),
                candidateWillToRelocate = submitData["candidateWillToRelocate"].toString().toDouble().toInt(),
                candidateCoreSkills = submitData["candidateCoreSkills"].toString(),
                candidateSuppSkills = submitData["candidateSuppSkills"].toString(),
            )
        }

        return@withContext listOf(true, candidate)
    }

}