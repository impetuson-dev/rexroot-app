package com.impetuson.rexroot

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.impetuson.rexroot.databinding.ActivityJobreqBinding
import com.impetuson.rexroot.databinding.BottomsheetApplyResumeBinding
import com.impetuson.rexroot.view.jobreq.JobreqActionsFragment
import com.impetuson.rexroot.view.jobreq.JobreqDetailsFragment
import com.impetuson.rexroot.view.jobreq.JobreqSubmissionsFragment
import com.impetuson.rexroot.view.jobreq.JobreqViewPagerAdapter
import com.impetuson.rexroot.viewmodel.jobreq.JobreqViewModel
import com.impetuson.rexroot.viewmodel.jobreq.PartnerSmsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException


class JobreqActivity: AppCompatActivity() {
    private val PDF_REQUEST_CODE = 123
    private var alert: Boolean = false
    var jobId: String = ""
    var jobRole: String = ""
    var reqJobExp: String = ""
    var jobSalary: String = ""

    private lateinit var alertDialog: Dialog
    private lateinit var dialogBinding: BottomsheetApplyResumeBinding
    private lateinit var binding: ActivityJobreqBinding
    private lateinit var jobreqViewPageAdapter: JobreqViewPagerAdapter
    private val jobreqViewModel: JobreqViewModel by viewModels()
    private val partnerSmsViewModel : PartnerSmsViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer
    private val mClient = OkHttpClient()
    private lateinit var  mobile_no : String
    private lateinit var  otp_no : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobreqBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mediaPlayer = MediaPlayer.create(this, R.raw.file_upload_success)

        alertDialog = MaterialAlertDialogBuilder(this).setTitle("Uploading Resume(s)").setMessage("Please wait...").setCancelable(false).create()

        jobId = getIntent().extras?.getString("jobid") ?: "Not available"
        jobRole = getIntent().extras?.getString("jobRole") ?: "Not available"
        reqJobExp = getIntent().extras?.getString("reqJobExp") ?: "Not available"
        jobSalary = getIntent().extras?.getString("jobSalary") ?: "Not available"

        Log.d("Activity Data Received", jobId)
        Log.d("Activity Data Received", jobRole)
        Log.d("Activity Data Received", reqJobExp)
        Log.d("Activity Data Received", jobSalary)

        jobreqViewPageAdapter = JobreqViewPagerAdapter(this)

        jobreqViewPageAdapter.addFragment(JobreqDetailsFragment())
        jobreqViewPageAdapter.addFragment(JobreqSubmissionsFragment(jobId))
        jobreqViewPageAdapter.addFragment(JobreqActionsFragment())

        jobreqViewModel.fetchDataSharedPref(this.getSharedPreferences("profiledata", MODE_PRIVATE))
        jobreqViewModel.saveToRecentJobs(this.getSharedPreferences("recentjobs", MODE_PRIVATE), jobId)
        jobreqViewModel.jobId = jobId
        jobreqViewModel.jobRole = jobRole
        jobreqViewModel.reqjobExp = reqJobExp
        jobreqViewModel.jobSalary = jobSalary
        Log.d("jobid",jobId)

        binding.apply{
            lifecycleOwner = this@JobreqActivity
            jobreqviewmodel = jobreqViewModel
            applyJob.setOnClickListener {
                applyToJob()
            }

            cvGoback.setOnClickListener {
                onBackPressed()
            }

            viewPager.adapter = jobreqViewPageAdapter
            TabLayoutMediator(jobreqTablayout, viewPager){ tab,position ->
                when(position){
                    0 -> {
                        tab.text = "Job Details"
                    }
                    1 -> {
                        tab.text = "Submissions"
                    }
                    2 -> {
                        tab.text = "Actions"
                    }
                }
            }.attach()

            loadingAnimation.visibility = View.VISIBLE
            body.visibility = View.GONE
            MainScope().launch {
                jobreqViewModel.fetchRealtimeDB()
                loadingAnimation.visibility = View.GONE
                body.visibility = View.VISIBLE
            }

            btnChoose.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/pdf"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(Intent.createChooser(intent, "Select Resume"), PDF_REQUEST_CODE)
            }

            btnSubmit.setOnClickListener {
                MainScope().launch {
                    viewTopShadow.visibility = View.GONE
                    progressIndicator.visibility = View.VISIBLE
                    val uploadMsg = jobreqViewModel.btnSubmitHandler(contentResolver)

                    mediaPlayer.start()

                    Snackbar.make(llSnackbar, uploadMsg, Snackbar.LENGTH_SHORT).setAnchorView(llFooter).show()

                    progressIndicator.visibility = View.GONE
                    viewTopShadow.visibility = View.VISIBLE

                    if (alert){
                        alert = false
                        alertDialog.dismiss()
                    }

                }
            }
        }
    }


    fun applyToJob(){
        dialogBinding = BottomsheetApplyResumeBinding.inflate(layoutInflater)
        val applyBottomSheet = BottomSheetDialog(this)
        applyBottomSheet.setContentView(dialogBinding.root)
        applyBottomSheet.behavior.isDraggable = true
        applyBottomSheet.setCanceledOnTouchOutside(false)
        dialogBinding.lifecycleOwner = this

        partnerSmsViewModel.partners_name.observe(this,{
            dialogBinding.partnerName.editText?.setText(it)
        })

        partnerSmsViewModel.mobile_no.observe(this,{
            dialogBinding.etMobilenumber2.editText?.setText(it)
            mobile_no = it
        })

        partnerSmsViewModel.otp_no.observe(this,{
            dialogBinding.etOtpnumber.editText?.setText(it)
        })

        partnerSmsViewModel.choice.observe(this,{
            if(it == true){
                dialogBinding.yourself.isChecked = true
                dialogBinding.attachResume.text = "Your resume"
                dialogBinding.attachResume.setBackgroundColor(getResources().getColor(R.color.violet))
                dialogBinding.partnerName.visibility = View.GONE
                dialogBinding.sendOtp.visibility = View.GONE
                dialogBinding.verifyOtp.visibility = View.GONE
            }
            else{
                dialogBinding.yourPartner.isChecked = true
                dialogBinding.attachResume.text = "Partner's resume"
                dialogBinding.attachResume.setBackgroundColor(getResources().getColor(R.color.violet))
                dialogBinding.partnerName.visibility = View.VISIBLE
                dialogBinding.sendOtp.visibility = View.VISIBLE
                dialogBinding.verifyOtp.visibility = View.VISIBLE
            }
        })

        partnerSmsViewModel.send_button_status.observe(this,{
            if(it == true){
                dialogBinding.sendOtpButton.setBackgroundColor(getResources().getColor(R.color.violet))
                dialogBinding.verifyOtpButton.setBackgroundColor(getResources().getColor(R.color.light_grey))
                dialogBinding.sendOtpButton.isEnabled = true
                dialogBinding.verifyOtpButton.isEnabled = false
            }
            else{
                dialogBinding.sendOtpButton.setBackgroundColor(getResources().getColor(R.color.light_grey))
                dialogBinding.verifyOtpButton.setBackgroundColor(getResources().getColor(R.color.violet))
                dialogBinding.sendOtpButton.isEnabled = false
                dialogBinding.verifyOtpButton.isEnabled = true
                dialogBinding.sendOtpButton.text = "Sent"
            }
        })

        partnerSmsViewModel.verify_button_status.observe(this,{
            if(it == true){
                dialogBinding.verifyOtpButton.setBackgroundColor(getResources().getColor(R.color.violet))
                dialogBinding.verifyOtpButton.isEnabled = true
            }
            else{
                dialogBinding.verifyOtpButton.setBackgroundColor(getResources().getColor(R.color.light_grey))
                dialogBinding.verifyOtpButton.isEnabled = false
            }
        })

        partnerSmsViewModel.verify_status.observe(this,{
            dialogBinding.verifyOtpButton.text = it
        })

        dialogBinding.yourself.setOnClickListener {
            partnerSmsViewModel.update_choice(true)
        }

        dialogBinding.yourPartner.setOnClickListener {
            partnerSmsViewModel.update_choice(false)
            dialogBinding.partnerName.visibility = View.VISIBLE
            dialogBinding.sendOtp.visibility = View.VISIBLE
            dialogBinding.verifyOtp.visibility = View.VISIBLE
        }

        dialogBinding.attachResume.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select Resume"), PDF_REQUEST_CODE)
        }

        dialogBinding.sendOtpButton.setOnClickListener {
            val mob_no = dialogBinding.etMobilenumber2.editText?.text.toString()
            val name = dialogBinding.partnerName.editText?.text.toString()
            partnerSmsViewModel.update_mobile_no(mob_no,name)
            Log.d("Mobile No",mob_no)
            if(mob_no.length == 13){
                try{
                    val response = post(mob_no)
                    if (response != null) {
                        Log.d("SMS Response",response)
                    }
                    partnerSmsViewModel.update_verify_button(true)
                }
                catch (e: IOException){
                    Log.d("Send Msg Exception",e.printStackTrace().toString())
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Invalid Number Format",Toast.LENGTH_LONG).show();
            }
        }

        dialogBinding.verifyOtpButton.setOnClickListener {
            val ot_no = dialogBinding.etOtpnumber.editText?.text.toString()
            partnerSmsViewModel.update_otp_no(ot_no)
            Log.d("OTP no",ot_no)
            if(ot_no.length == 6){
                try{
                    val response = otp(mobile_no,ot_no)
                    if (response != null) {
                        Log.d("OTP Response",response)
                    }
                    partnerSmsViewModel.update_verify_button(false)
                }
                catch (e: IOException){
                    Log.d("OTP Exception",e.printStackTrace().toString())
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Enter 6 digits",Toast.LENGTH_LONG).show();
            }
        }
        applyBottomSheet.show()
    }

    @Throws(IOException::class)
    fun post(mobile_no:String):String?{
        val client = OkHttpClient().newBuilder().build()
        val mediaType = MediaType.parse("text/plain")
        val body = RequestBody.create(mediaType, "")
        val request = Request.Builder()
            .url("https://2factor.in/API/V1/15a2d98c-2dea-11ee-addf-0200cd936042/SMS/${mobile_no}/AUTOGEN/OTP1")
            .method("POST", body)
            .build()
        val response = client.newCall(request).execute()
        val response_body = response.body()?.string()
        response.body()?.close()
        return response_body
    }

    @Throws(IOException::class)
    fun otp(mobile_no: String,otp_no:String):String?{
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType = MediaType.parse("text/plain")
        val body = RequestBody.create(mediaType, "")
        val request = Request.Builder()
            .url("https://2factor.in/API/V1/15a2d98c-2dea-11ee-addf-0200cd936042/SMS/VERIFY3/${mobile_no}/${otp_no}")
            .method("POST", body)
            .build()
        val response: Response = client.newCall(request).execute()
        val response_body = response.body()?.string()
        response.body()?.close()
        return response_body
    }

    override fun onBackPressed() {
        if (binding.progressIndicator.visibility == View.VISIBLE){
            alert = true
            alertDialog.show()
        } else {
            finish()
            super.onBackPressed()
        }
    }
}