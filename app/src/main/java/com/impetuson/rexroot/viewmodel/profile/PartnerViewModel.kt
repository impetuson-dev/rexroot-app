package com.impetuson.rexroot.viewmodel.profile

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.impetuson.rexroot.model.jobreq.SubmissionsModelClass
import com.impetuson.rexroot.model.profile.PartnerModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PartnerViewModel: ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    val _userId = MutableLiveData<String>("")

    private var _partnerModel = MutableLiveData<PartnerModelClass>(PartnerModelClass())
    val partnerModel: LiveData<PartnerModelClass> = _partnerModel

    fun saveDetails(){
        val partner = PartnerModelClass(
            partnerFullName = partnerModel.value?.partnerFullName ?: "",
            partnerEmail = partnerModel.value?.partnerEmail ?: "",
            partnerMobileNumber = partnerModel.value?.partnerMobileNumber ?: "",
            partnerAddress = partnerModel.value?.partnerAddress ?: "",
            partnerIndDomain = partnerModel.value?.partnerIndDomain ?: "",
            partnerSector = partnerModel.value?.partnerSector ?: ""
        )
        Log.d("partner","${partner}")
        storeDataToFirestore(partner)
    }

    fun setDetails(partner: PartnerModelClass) {
        _partnerModel.value = partner
    }

    private fun storeDataToFirestore(partner: PartnerModelClass) {
        val userId = auth.currentUser!!.uid
        var storeMsg: String = ""
        var storeStatus: Boolean = false

        val partnerdata = hashMapOf(
            "partnerdata" to objectToMap(partner)
        )

        Log.d("partnerdata","${partnerdata}")


        try{
            db.collection("users").document(userId).set(partnerdata, SetOptions.merge())
            Log.d("FirestoreDB","Partner Data added: $userId")
            storeStatus = true
            storeMsg = "Partner details updated successfully"
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

        var partner = PartnerModelClass()

        val documentSnapshot = db.collection("users").document(_userId.value.toString()).get()
            .addOnFailureListener {  e ->
                Log.d("Firebase Firestore","ERROR: ${e.message}")
            }
            .await()

        if (documentSnapshot != null){
            val submitData = documentSnapshot.get("partnerdata") as? Map<String,Any>

            Log.d("Partner profile", "Partner Data: ${submitData.toString()}")

            if (submitData.isNullOrEmpty()) {
                return@withContext listOf(false, partner)
            }

            Log.d("partnerdata",submitData.toString())

            partner = PartnerModelClass(
                partnerFullName = submitData["partnerFullName"].toString(),
                partnerEmail = submitData["partnerEmail"].toString(),
                partnerMobileNumber = submitData["partnerMobileNumber"].toString(),
                partnerAddress = submitData["partnerAddress"].toString(),
                partnerIndDomain = submitData["partnerIndDomain"].toString(),
                partnerSector = submitData["partnerSector"].toString()
            )
        }

        return@withContext listOf(true, partner)
    }

}