package com.impetuson.rexroot

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.impetuson.rexroot.databinding.ActivityJobsBinding
import com.impetuson.rexroot.model.jobreq.JobReqModelClass
import com.impetuson.rexroot.viewmodel.main.JobReqRecyclerViewAdapter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobsActivity: AppCompatActivity() {

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
            val jobsearch = intent.getStringExtra("jobsearch")
            val location = intent.getStringExtra("location")
            tvJobsearch.text = "Jobs for $jobsearch, $location"

            ivGoback.setOnClickListener {
                onBackPressed()
            }

            rvJobs.layoutManager = LinearLayoutManager(this@JobsActivity)
            jobReqList = ArrayList<JobReqModelClass>()
            jobreqadapter = JobReqRecyclerViewAdapter(jobReqList)
            rvJobs.adapter = jobreqadapter

            MainScope().launch {
                recyclerViewLoader()
                pbLoading.visibility = View.GONE

                if (jobReqList.isEmpty()){ tvNoresults.visibility = View.VISIBLE }
            }
        }
    }

    private suspend fun recyclerViewLoader() = withContext(Dispatchers.IO){
        // Since the .addChildEventListener() does not return any object that can be awaited
        val jobReqDeferred = CompletableDeferred<Unit>()

        firebaseDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val jobReqCard = snapshot.getValue(JobReqModelClass::class.java)
                jobReqList.add(0,jobReqCard!!)
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
}