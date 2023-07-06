package com.impetuson.rexroot

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeDrawable.BOTTOM_END
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.impetuson.rexroot.databinding.ActivityJobreqBinding
import com.impetuson.rexroot.view.jobreq.JobreqActionsFragment
import com.impetuson.rexroot.view.jobreq.JobreqDetailsFragment
import com.impetuson.rexroot.view.jobreq.JobreqSubmissionsFragment
import com.impetuson.rexroot.view.jobreq.JobreqViewPagerAdapter
import com.impetuson.rexroot.viewmodel.jobreq.JobreqViewModel
import com.impetuson.rexroot.viewmodel.jobreq.SubmissionsViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class JobreqActivity: AppCompatActivity() {

    private val PDF_REQUEST_CODE = 123

    private var alert: Boolean = false
    var jobId: String = ""

    private lateinit var alertDialog: Dialog
    private lateinit var binding: ActivityJobreqBinding
    private lateinit var jobreqViewPageAdapter: JobreqViewPagerAdapter
    private val jobreqViewModel: JobreqViewModel by viewModels()

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobreqBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mediaPlayer = MediaPlayer.create(this, R.raw.file_upload_success)

        alertDialog = MaterialAlertDialogBuilder(this).setTitle("Uploading Resume(s)").setMessage("Please wait...").setCancelable(false).create()
        jobId = intent.getStringExtra("jobid") ?: ""
        jobreqViewPageAdapter = JobreqViewPagerAdapter(this)

        jobreqViewPageAdapter.addFragment(JobreqDetailsFragment())
        jobreqViewPageAdapter.addFragment(JobreqSubmissionsFragment(jobId))
        jobreqViewPageAdapter.addFragment(JobreqActionsFragment())

        jobreqViewModel.fetchDataSharedPref(this.getSharedPreferences("profiledata", MODE_PRIVATE))
        jobreqViewModel.saveToRecentJobs(this.getSharedPreferences("recentjobs", MODE_PRIVATE), jobId)
        jobreqViewModel.jobId = jobId
        Log.d("jobid",jobId)

        binding.apply{
            lifecycleOwner = this@JobreqActivity
            jobreqviewmodel = jobreqViewModel

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            jobreqViewModel.getSelectedFiles(data)
        }
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