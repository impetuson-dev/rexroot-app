package com.impetuson.rexroot.view.jobs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.ActivityJobsBinding
import com.impetuson.rexroot.model.jobreq.JobReqModelClass
import com.impetuson.rexroot.viewmodel.main.JobReqRecyclerViewAdapter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobsActivity: AppCompatActivity() {

    private var jobsearch: String = ""
    private var location: String = ""

    private lateinit var jobReqList: ArrayList<JobReqModelClass>
    private lateinit var jobreqadapter: JobReqRecyclerViewAdapter
    private lateinit var binding: ActivityJobsBinding
    private var firebaseDB = FirebaseDatabase.getInstance().getReference("jobreq")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.apply {
            jobsearch = intent.getStringExtra("jobsearch").toString()
            location = intent.getStringExtra("location").toString()
            tvJobsearch.text = "Jobs for ${jobsearch.trim()}, ${location.trim()}"

            ivGoback.setOnClickListener {
                onBackPressed()
            }

            rvJobs.layoutManager = LinearLayoutManager(this@JobsActivity)
            jobReqList = ArrayList<JobReqModelClass>()
            jobreqadapter = JobReqRecyclerViewAdapter(jobReqList)
            rvJobs.adapter = jobreqadapter

            MainScope().launch {
                recyclerViewLoader(jobsearch,location)
                pbLoading.visibility = View.GONE

                if (jobReqList.isEmpty()){ tvNoresults.visibility = View.VISIBLE }
            }

            llSearch.setOnClickListener {
                etJobsearch.setText(jobsearch)
                etLocation.setText(location)
                llSearchfields.visibility = View.VISIBLE
            }

            btnCancel.setOnClickListener {
                it.hideKeyboard()
                llSearchfields.visibility = View.GONE
                llSearch.visibility = View.VISIBLE
            }

            btnSearch.setOnClickListener {
                it.hideKeyboard()
                jobReqList.clear()
                llSearchfields.visibility = View.GONE

                jobsearch = etJobsearch.text.toString()
                location = etLocation.text.toString()
                tvJobsearch.text = "Jobs for ${jobsearch.trim()}, ${location.trim()}"
                pbLoading.visibility = View.VISIBLE

                MainScope().launch {
                    recyclerViewLoader(jobsearch,location)
                    pbLoading.visibility = View.GONE
                    if (jobReqList.isEmpty()){ tvNoresults.visibility = View.VISIBLE }
                }
            }

            cvFilter.setOnClickListener {
                val modalBottomSheet = FilterFragment()
                modalBottomSheet.show(supportFragmentManager, FilterFragment.TAG)
            }
        }
    }

    private suspend fun recyclerViewLoader(jobsearch: String, location: String) = withContext(Dispatchers.IO){
        // Since the .addChildEventListener() does not return any object that can be awaited
        val jobReqDeferred = CompletableDeferred<Unit>()

        firebaseDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val jobReqCard = snapshot.getValue(JobReqModelClass::class.java)

                if (jobReqCard != null) {
                    if (searchFilter(jobReqCard,jobsearch,location)){
                        jobReqList.add(0,jobReqCard)
                    }
                }

                if (jobReqList.isEmpty()){
                    binding.tvNoresults.visibility = View.VISIBLE
                } else{
                    binding.tvNoresults.visibility = View.GONE
                }


                binding!!.rvJobs.adapter?.notifyDataSetChanged()
                binding.pbLoading.visibility = View.GONE
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val jobReqCard = snapshot.getValue(JobReqModelClass::class.java)
                val index = jobReqList.indexOfFirst { it.jobid == jobReqCard?.jobid }
                if (index != -1) {
                    jobReqList[index] = jobReqCard!!
                    binding.rvJobs.adapter?.notifyItemChanged(index)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val jobReqCard = snapshot.getValue(JobReqModelClass::class.java)
                jobReqList.remove(jobReqCard)
                binding.rvJobs.adapter?.notifyDataSetChanged()

                binding.pbLoading.visibility = View.GONE
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseDB", "Error Occurred: $error")
                Toast.makeText(this@JobsActivity, "Database error occurred", Toast.LENGTH_SHORT).show()
                jobReqDeferred.completeExceptionally(error.toException())
            }

        })

        jobReqDeferred.await()
    }

    private fun searchFilter(jobReqCard: JobReqModelClass, userJobSearch: String, userJobLocation: String): Boolean{
        val jobRole = jobReqCard.jobrole
        val compName = jobReqCard.compname
        val compLocation = jobReqCard.complocation

        if (jobRole.contains(userJobSearch, true) && compLocation.contains(userJobLocation, true)){
            return true
        } else if (compName.contains(userJobSearch,true) && compLocation.contains(userJobLocation, true)) {
            return true
        }

        return false
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}