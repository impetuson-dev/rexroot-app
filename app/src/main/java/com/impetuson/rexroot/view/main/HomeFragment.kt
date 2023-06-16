package com.impetuson.rexroot.view.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.impetuson.rexroot.ProfileActivity
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentHomeBinding
import com.impetuson.rexroot.model.profile.JobReqModelClass
import com.impetuson.rexroot.viewmodel.main.JobReqRecyclerViewAdapter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var jobReqList: ArrayList<JobReqModelClass>
    private lateinit var jobreqadapter: JobReqRecyclerViewAdapter
    private lateinit var firebaseDB : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        changeStatusBarColor()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner

            btnMyprofile.setOnClickListener {
                val intent = Intent(context,ProfileActivity::class.java)
                startActivity(intent)
            }

            rvJobreq.layoutManager = LinearLayoutManager(requireContext())
            jobReqList = ArrayList<JobReqModelClass>()
            jobreqadapter = JobReqRecyclerViewAdapter(jobReqList)
            rvJobreq.adapter = jobreqadapter

            loadingAnimation.visibility = View.VISIBLE

            firebaseDB = FirebaseDatabase.getInstance().getReference("jobreq")

            MainScope().launch {
                recyclerViewLoader()
                loadingAnimation.visibility = View.GONE

                if (jobReqList.isEmpty()){ noresultsAnimation.visibility = View.VISIBLE }
            }
        }

        // Exit app
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
    }

    private fun changeStatusBarColor() {
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#e5e5ff")
    }

    private suspend fun recyclerViewLoader() = withContext(Dispatchers.IO){
        // Since the .addChildEventListener() does not return any object that can be awaited
        val jobReqDeferred = CompletableDeferred<Unit>()

        firebaseDB.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val jobReqCard = snapshot.getValue(JobReqModelClass::class.java)
                jobReqList.add(0,jobReqCard!!)
                binding!!.rvJobreq.adapter?.notifyDataSetChanged()

                binding!!.loadingAnimation.visibility = View.GONE
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val jobReqCard = snapshot.getValue(JobReqModelClass::class.java)
                val index = jobReqList.indexOfFirst { it.jobid == jobReqCard?.jobid }
                if (index != -1) {
                    jobReqList[index] = jobReqCard!!
                    binding!!.rvJobreq.adapter?.notifyItemChanged(index)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val jobReqCard = snapshot.getValue(JobReqModelClass::class.java)
                jobReqList.remove(jobReqCard)
                binding!!.rvJobreq.adapter?.notifyDataSetChanged()

                binding!!.loadingAnimation.visibility = View.GONE
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseDB", "Error Occurred: $error")
                Toast.makeText(requireContext(), "Database error occurred", Toast.LENGTH_SHORT).show()
                jobReqDeferred.completeExceptionally(error.toException())
            }

        })

        jobReqDeferred.await()
    }

}