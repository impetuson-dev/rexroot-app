package com.impetuson.rexroot.view.jobs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.ActivityJobsBinding
import com.impetuson.rexroot.model.jobreq.JobReqModelClass
import com.impetuson.rexroot.viewmodel.jobreq.JobreqViewModel
import com.impetuson.rexroot.viewmodel.jobs.JobsViewModel
import com.impetuson.rexroot.viewmodel.main.JobReqRecyclerViewAdapter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobsActivity: AppCompatActivity() {

    private lateinit var jobsearch: String
    private lateinit var location: String

    private lateinit var jobReqList: ArrayList<JobReqModelClass>
    private lateinit var jobreqadapter: JobReqRecyclerViewAdapter
    private lateinit var binding: ActivityJobsBinding
    private var firebaseDB = FirebaseDatabase.getInstance().getReference("jobreq")

    private val jobsViewModel: JobsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        jobsViewModel._jobSearch.value = intent.getStringExtra("jobsearch").toString()
        jobsViewModel._jobLocation.value = intent.getStringExtra("location").toString()

        jobsearch = jobsViewModel._jobSearch.value ?: ""
        location = jobsViewModel._jobLocation.value ?: ""

        binding.apply {
            tvJobsearch.text = "Jobs for ${jobsViewModel._jobSearch.value}, ${jobsViewModel._jobLocation.value}"

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
                refreshList()
            }

            cvFilter.setOnClickListener {
                showFilterBottomDialog()
            }
        }
    }

    private fun showFilterBottomDialog() {
        var paymentStart: String = jobsViewModel._paymentStart.value.toString()
        var paymentEnd: String = if (jobsViewModel._paymentEnd.value == Int.MAX_VALUE) "" else jobsViewModel._paymentEnd.value.toString()

        val filterBottomSheet = BottomSheetDialog(this)
        filterBottomSheet.setContentView(R.layout.bottomsheet_filter)
        val filterBottomSheetBehavior = filterBottomSheet.behavior

        filterBottomSheet.findViewById<EditText>(R.id.et_payment_start)!!.setText(paymentStart)
        filterBottomSheet.findViewById<EditText>(R.id.et_payment_end)!!.setText(paymentEnd)

        filterBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        filterBottomSheetBehavior.isDraggable = false
        filterBottomSheet.findViewById<MaterialButton>(R.id.btn_cancel)!!.setOnClickListener {
            filterBottomSheet.dismiss()
        }

        filterBottomSheet.findViewById<MaterialButton>(R.id.btn_filter)!!.setOnClickListener {
            paymentStart = filterBottomSheet.findViewById<EditText>(R.id.et_payment_start)?.text.toString()
            paymentEnd = filterBottomSheet.findViewById<EditText>(R.id.et_payment_end)?.text.toString()

            jobsViewModel._paymentIsEnabled.value = true
            jobsViewModel._paymentStart.value = if (paymentStart.isEmpty()) 0 else paymentStart.toInt()
            jobsViewModel._paymentEnd.value = if (paymentEnd.isEmpty()) Int.MAX_VALUE else paymentEnd.toInt()

            Log.d("Filter BottomSheet","PAYMENT start: $paymentStart and end: $paymentEnd")

            refreshList()
            filterBottomSheet.dismiss()
        }


        filterBottomSheet.show()
    }


    private suspend fun recyclerViewLoader(jobsearch: String, location: String) = withContext(Dispatchers.IO){
        // Since the .addChildEventListener() does not return any object that can be awaited
        val jobReqDeferred = CompletableDeferred<Unit>()

        firebaseDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val jobReqCard = snapshot.getValue(JobReqModelClass::class.java)

                if (jobReqCard != null) {
                    if (searchFilter(jobReqCard,jobsearch,location)){
                        Log.d("payment","JOB FILTERED: $jobReqCard")
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

        if (jobRole.contains(userJobSearch, true) && compLocation.contains(userJobLocation, true) && paymentFilter(jobReqCard.priceperclosure)){
            return true
        } else if (compName.contains(userJobSearch,true) && compLocation.contains(userJobLocation, true) && paymentFilter(jobReqCard.priceperclosure)) {
            return true
        }

        return false
    }

    private fun paymentFilter(paymentPerClosure: String): Boolean {
        var payment = 0

        try {
            val regex = Regex("[^0-9]")
            payment = regex.replace(paymentPerClosure, "").toInt()
        } catch (e: Exception) {
            return true
        }

        val paymentStart = jobsViewModel._paymentStart.value ?: 0
        val paymentEnd = jobsViewModel._paymentEnd.value ?: Int.MAX_VALUE

        Log.d("payment","$payment in $paymentStart to $paymentEnd")
        return payment in paymentStart..paymentEnd
    }

    private fun refreshList() {
        jobReqList.clear()
        binding.llSearchfields.visibility = View.GONE

        binding.pbLoading.visibility = View.VISIBLE

        MainScope().launch {
            recyclerViewLoader(jobsearch,location)
            binding.pbLoading.visibility = View.GONE
            if (jobReqList.isEmpty()){ binding.tvNoresults.visibility = View.VISIBLE }
        }
    }


    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}