package com.impetuson.rexroot

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.impetuson.rexroot.databinding.ActivityJobreqBinding
import com.impetuson.rexroot.view.jobreq.JobreqActionsFragment
import com.impetuson.rexroot.view.jobreq.JobreqDetailsFragment
import com.impetuson.rexroot.view.jobreq.JobreqSubmissionsFragment
import com.impetuson.rexroot.view.jobreq.JobreqViewPagerAdapter
import com.impetuson.rexroot.viewmodel.jobreq.JobreqViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class JobreqActivity: AppCompatActivity() {

    private lateinit var jobId: String
    private lateinit var binding: ActivityJobreqBinding
    private lateinit var jobreqViewPageAdapter: JobreqViewPagerAdapter
    private val viewmodel: JobreqViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobreqBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        jobId = intent.getStringExtra("jobid") ?: ""
        jobreqViewPageAdapter = JobreqViewPagerAdapter(this)

        jobreqViewPageAdapter.addFragment(JobreqDetailsFragment())
        jobreqViewPageAdapter.addFragment(JobreqSubmissionsFragment())
        jobreqViewPageAdapter.addFragment(JobreqActionsFragment())

        viewmodel.fetchDataSharedPref(this.getSharedPreferences("profiledata", MODE_PRIVATE))
        viewmodel.jobId = jobId

        binding.apply{
            lifecycleOwner = this@JobreqActivity
            jobreqviewmodel = viewmodel

            ivGoback.setOnClickListener {
                onBackPressed()
            }

            viewPager.adapter = jobreqViewPageAdapter

            TabLayoutMediator(jobreqTablayout, viewPager){ tab,position ->
                when(position){
                    0 -> tab.text = "Job Details"
                    1 -> tab.text = "Submissions"
                    2 -> tab.text = "Actions"
                }
            }.attach()

            loadingAnimation.visibility = View.VISIBLE
            body.visibility = View.GONE
            MainScope().launch {
                viewmodel.fetchRealtimeDB()
                loadingAnimation.visibility = View.GONE
                body.visibility = View.VISIBLE
            }

        }



    }
}