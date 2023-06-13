package com.impetuson.rexroot.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.impetuson.rexroot.model.profile.PartnerModelClass
import kotlinx.coroutines.tasks.await

class PartnerViewModel: ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _partnerModel = MutableLiveData<PartnerModelClass>(PartnerModelClass("","","","","",""))
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

    fun objectToMap(obj: Any): Map<String, Any?> {
        val gson = Gson()
        val json = gson.toJsonTree(obj)
        return gson.fromJson(json, Map::class.java) as Map<String, Any?>
    }

}