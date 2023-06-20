package com.impetuson.rexroot.view.jobreq

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentJobreqsubmissionsBinding
import com.impetuson.rexroot.model.jobreq.SubmissionsModelClass
import com.impetuson.rexroot.viewmodel.jobreq.SubmissionsRecyclerViewAdapter
import com.impetuson.rexroot.viewmodel.jobreq.SubmissionsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class JobreqSubmissionsFragment(jobId: String) : Fragment() {

    private var binding: FragmentJobreqsubmissionsBinding ?= null
    private lateinit var resumeList: List<SubmissionsModelClass>
    private lateinit var submissionsAdapter: SubmissionsRecyclerViewAdapter
    private val viewmodel = SubmissionsViewModel(jobId)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jobreqsubmissions, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            context?.let { viewmodel.fetchDataSharedPref(it.getSharedPreferences("profiledata",MODE_PRIVATE)) }

            rvSubmissions.layoutManager = LinearLayoutManager(requireContext())
            MainScope().launch {
                resumeList = viewmodel.fetchDataFromFirestore()
                submissionsAdapter = SubmissionsRecyclerViewAdapter(resumeList)
                rvSubmissions.adapter = submissionsAdapter

                if (resumeList.isEmpty()){
                    tvNoresults.visibility = View.VISIBLE
                }
            }
        }
    }
}