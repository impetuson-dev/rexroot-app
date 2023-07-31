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
    private lateinit var  mobile_no : String
    private lateinit var  partner_name : String

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
            partner_name = it
        })

        partnerSmsViewModel.mobile_no.observe(this,{
            dialogBinding.etMobilenumber2.editText?.setText(it)
            mobile_no = it
        })

        partnerSmsViewModel.choice.observe(this,{
            if(it == true){
                dialogBinding.yourself.isChecked = true
                dialogBinding.attachResume.text = "Your resume"
                dialogBinding.attachResume.setBackgroundColor(getResources().getColor(R.color.violet))
                dialogBinding.partnerName.visibility = View.GONE
                dialogBinding.sendOtp.visibility = View.GONE
            }
            else{
                dialogBinding.yourPartner.isChecked = true
                dialogBinding.attachResume.text = "Partner's resume"
                dialogBinding.attachResume.setBackgroundColor(getResources().getColor(R.color.violet))
                dialogBinding.partnerName.visibility = View.VISIBLE
                dialogBinding.sendOtp.visibility = View.VISIBLE
            }
        })

        dialogBinding.yourself.setOnClickListener {
            partnerSmsViewModel.update_choice(true)
        }

        dialogBinding.yourPartner.setOnClickListener {
            partnerSmsViewModel.update_choice(false)
        }

        dialogBinding.attachResume.setOnClickListener {
            val mob_no = dialogBinding.etMobilenumber2.editText?.text.toString()
            val name = dialogBinding.partnerName.editText?.text.toString()
            partnerSmsViewModel.update_mobile_no(mob_no,name)
            Log.d("Name",partner_name)
            Log.d("Mobile No",mobile_no)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select Resume"), PDF_REQUEST_CODE)
        }
        applyBottomSheet.show()
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