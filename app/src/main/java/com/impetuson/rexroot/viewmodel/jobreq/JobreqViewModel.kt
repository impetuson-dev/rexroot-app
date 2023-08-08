package com.impetuson.rexroot.viewmodel.jobreq

import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.impetuson.rexroot.BuildConfig
import com.impetuson.rexroot.model.jobreq.JobReqModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class JobreqViewModel : ViewModel() {
    private val firestoreDB = Firebase.firestore
    private lateinit var realtimeDB: DatabaseReference
    private lateinit var mediaPlayer: MediaPlayer

    val mail = BuildConfig.MAIL
    private var selectedFilesUri = mutableListOf<Uri>()
    private var selectedFilesNames = mutableListOf<String>()
    private var selectedUUIDFilesNames = mutableListOf<String>()

    private val _btnChooseText = MutableLiveData<String>("Choose Resume(s)")
    var btnChooseText: LiveData<String> = _btnChooseText

    private val _btnSubmitText = MutableLiveData<String>("Submit")
    var btnSubmitText: LiveData<String> = _btnSubmitText

    private val _btnSubmitStatus = MutableLiveData<Boolean>(false)
    var btnSubmitStatus: LiveData<Boolean> = _btnSubmitStatus

    private val _btnChooseStatus = MutableLiveData<Boolean>(true)
    var btnChooseStatus: LiveData<Boolean> = _btnChooseStatus

    private val _jobreqModel = MutableLiveData<JobReqModelClass>()
    var jobreqModel: LiveData<JobReqModelClass> = _jobreqModel

    private val _uploadProgress = MutableLiveData<Int>()

    var userId: String = ""
    var userMail: String = ""
    var userName: String = ""
    var partnerName: String = ""
    var partnerPhoneNo: String = ""
    var userPhoneNo: String = ""
    var jobId: String = ""
    var jobRole: String = ""
    var reqjobExp: String = ""
    var jobSalary: String = ""
    var price: String = ""
    var status : Boolean = true


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
        userName = sharedPreferences.getString("username","").toString()
        userMail = sharedPreferences.getString("useremail","").toString()
        userPhoneNo = sharedPreferences.getString("usermobilenumber","").toString()
    }

    private fun convertMapToObject(jobDataMap: Map<String,Any>): JobReqModelClass{
        val gson = Gson()
        val jsonString = gson.toJson(jobDataMap)
        return gson.fromJson(jsonString, object : TypeToken<JobReqModelClass>() {}.type)
    }

    private fun calcJobPostedTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyMMddHHmmss")
        return currentDateTime.format(formatter).toString()
    }

    private fun calcJobPostedDate(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return currentDateTime.format(formatter).toString()
    }

    fun getSelectedFiles(data: Intent?): Boolean{
        selectedFilesUri.clear()
        selectedFilesNames.clear()
        selectedUUIDFilesNames.clear()

        // Single file selection
        data?.data?.let{ selectedFilesUri.add(it) }

        // Multiple files selection
        data?.clipData?.let{
            for (i in 0 until it.itemCount){
                selectedFilesUri.add(it.getItemAt(i).uri)
            }
        }

        _btnChooseText.value = "Reselect Resume(s)"
        _btnSubmitStatus.value = selectedFilesUri.isNotEmpty()
        return selectedFilesUri.isNotEmpty()
    }

    suspend fun btnSubmitHandler(contentResolver: ContentResolver): String{
        _btnSubmitText.value = "Uploading..."
        _btnChooseStatus.value = false
        _btnSubmitStatus.value = false
        val uploadMsg = uploadToFireStorage(selectedFilesUri, contentResolver)
        _btnSubmitText.value = "Submit"
        _btnChooseText.value = "Choose Resume(s)"
        _btnChooseStatus.value = true

        return uploadMsg
    }

    private suspend fun uploadToFireStorage(fileUris: List<Uri>, contentResolver: ContentResolver): String = withContext(Dispatchers.IO){
        val storageRef = Firebase.storage.reference
        var uploadMsg = ""
        var downloadUrl = ""

        fileUris.forEachIndexed { index, fileUri ->
            val fileName = UUID.randomUUID().toString()
            val pdfRef = storageRef.child("$userId/submissions/$jobId/$fileName")

            selectedUUIDFilesNames.add(fileName)
            val resumeName = getFileNameFromUri(fileUri, contentResolver)
            selectedFilesNames.add(resumeName)
            try {
                downloadUrl = pdfRef.putFile(fileUri).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    pdfRef.downloadUrl
                }.await().toString()

                //_uploadProgress.value =
                Log.d("Firebase Storage","Resume Uploaded successfully: $fileName")
                Log.d("Firebase Storage","Resume Download URL: $downloadUrl")

                storeDataToFirestore(fileName, resumeName, downloadUrl, index)
                if(partnerName.equals("") && partnerPhoneNo.equals("")){
                    val candPrice = ((price.toInt())/2).toString()
                    candidateMail(userName,userPhoneNo,jobRole,reqjobExp,jobSalary,downloadUrl)
                    candidateSucessMail(userName,jobRole,candPrice)
                }
                else{
                    partnerMail(userName,userPhoneNo,partnerName,partnerPhoneNo,jobRole,reqjobExp,jobSalary,downloadUrl)
                    partnerSucessMail(userName,partnerName,jobRole,price)
                }
            } catch(e: Exception) {
                uploadMsg = "Database error occurred"
                Log.d("Firebase Storage","Error: ${e.message}")
                throw e
            }
        }

        uploadMsg = "Resume(s) Uploaded successfully"
        uploadMsg
    }

    private fun storeDataToFirestore(resumeId: String, resumeName: String, resumeUrl: String, index: Int) {
        val postedTime = calcJobPostedTime()
        val postedDate = calcJobPostedDate()
        val resumeDataId = "$postedTime-$resumeId"

        val submitdata = mapOf<String,Any>(
            "submitdata" to mapOf(
                jobId to mapOf(
                    "resume" to mapOf(
                        resumeDataId to mapOf(
                            "resumeid" to resumeId,
                            "resumepost" to postedDate,
                            "resumestatus" to "0",
                            "partnermsg" to "Waiting for Approval",
                            "resumename" to resumeName,
                            "resumeurl" to resumeUrl
                        )
                    )
                )
            )
        )

        firestoreDB.collection("users").document(userId).set(submitdata, SetOptions.merge())

        val resumedata = mapOf<String,Any>(
            resumeDataId to mapOf(
                "resumetimestamp" to resumeDataId,
                "userid" to userId,
                "username" to userName,
                "resumeid" to resumeId,
                "resumepost" to postedDate,
                "resumestatus" to "0",
                "partnermsg" to "Waiting for Approval",
                "resumename" to resumeName,
                "resumeurl" to resumeUrl
            )
        )

        firestoreDB.collection("resumes").document(jobId).set(resumedata, SetOptions.merge())
    }

    private fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String {
        var fileName: String? = null
        val scheme = uri.scheme
        if (scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = it.getString(displayNameIndex)
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.lastPathSegment
        }
        return fileName ?: "N/A"
    }

    private fun fetchRecentJobs(sharedPreference: SharedPreferences): List<String>{
        val gson = Gson()
        val json = sharedPreference.getString("recent_list", "")


        val stringList: List<String> = gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        Log.d("recent_list", stringList.toString())
        return stringList
    }

    fun saveToRecentJobs(sharedPreferences: SharedPreferences, jobId: String){
        val existingJobs = fetchRecentJobs(sharedPreferences).toMutableList()
        val editor = sharedPreferences.edit()

        existingJobs.add(jobId)

        if (existingJobs.size > 10){ existingJobs.removeFirst() }

        val gson = Gson()
        val json = gson.toJson(existingJobs)

        editor.putString("recent_list", json)
        editor.apply()
    }

    fun candidateMail(userName: String, userPhoneNo: String,jobRole: String, reqjobExp: String, jobSalary: String, resumeURL: String){
        val mailData = hashMapOf(
            "to" to arrayListOf(mail),
            "message" to "Hello",
        )

        val nestedMailData = hashMapOf(
            "subject" to "Candidate Resume Uploaded",
            "html" to "Candidate Name: <strong>${userName}</strong><br><br>Candidate Phone Number: <strong>${userPhoneNo}</strong><br><br>Applied Job: <strong>${jobRole}</strong><br><br>Job Experience Required: <strong>${reqjobExp}</strong><br><br>Salary Package: <strong>${jobSalary}</strong><br><br>Click this link to view Candidate's Resume:<br>${resumeURL}",
        )

        mailData["message"] = nestedMailData

        firestoreDB.collection("mail").add(mailData)
    }

    fun partnerMail(userName: String, userPhoneNo: String,partnerName: String, partnerNo: String,jobRole: String, reqjobExp: String, jobSalary: String, resumeURL: String){
        val mailData = hashMapOf(
            "to" to arrayListOf(mail),
            "message" to "Hello",
        )

        val nestedMailData = hashMapOf(
            "subject" to "Partner Resume Uploaded",
            "html" to "Partner Name: <strong>${partnerName}</strong><br><br>Partner Phone Number: <strong>${partnerNo}</strong><br><br>Candidate Name: <strong>${userName}</strong><br><br>Candidate Phone No: <strong>${userPhoneNo}</strong><br><br>Applied Job: <strong>${jobRole}</strong><br><br>Job Experience Required: <strong>${reqjobExp}</strong><br><br>Salary Package: <strong>${jobSalary}</strong><br><br>Click this link to view the referred Partner's Resume:<br>${resumeURL}",
        )

        mailData["message"] = nestedMailData

        firestoreDB.collection("mail").add(mailData)
    }

    fun candidateSucessMail(userName: String, jobRole: String, price:String){
        val mailData = hashMapOf(
            "to" to arrayListOf(userMail),
            "message" to "Hello",
        )

        val nestedMailData = hashMapOf(
            "subject" to "Application Received",
            "html" to "Dear ${userName},<br><br> Thank you for taking the time to apply for our <strong>${jobRole}</strong> position." +
                    " We're currently in the process of taking applications and if you are selected to continue to the interview " +
                    "process, our human resources department will be in contact with you within 1-3 weeks. You will be rewared " +
                    "an amount of <strong>₹${price}</strong> after your completion of 3 months of work experience as <strong>${jobRole}</strong> " +
                    "in the respective company.<br><br>Thank you,<br><br>Your best regards,<br>Team REXROOT",
        )

        mailData["message"] = nestedMailData

        firestoreDB.collection("mail").add(mailData)
    }

    fun partnerSucessMail(userName: String, partnerName: String,jobRole: String, price:String){
        val mailData = hashMapOf(
            "to" to arrayListOf(userMail),
            "message" to "Hello",
        )

        val nestedMailData = hashMapOf(
            "subject" to "Application Received",
            "html" to "Dear ${userName},<br><br> Thank you for taking the time to apply for our <strong>${jobRole}</strong> position" +
                    " for your partner <strong>${partnerName}</strong>. We're currently in the process of taking applications and if he/she" +
                    " got selected to continue to the interview process, our human resources department will be in contact with you and your" +
                    " Partner within 1-3 weeks. You will be rewarded an amount of <strong>₹${price}</strong> when your partner completes 3 months" +
                    " of work experience as <strong>${jobRole}</strong> in the respective company.<br><br>Thank you,<br><br>Your best regards,<br>" +
                    "Team REXROOT",
        )

        mailData["message"] = nestedMailData

        firestoreDB.collection("mail").add(mailData)
    }
}