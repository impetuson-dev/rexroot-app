package com.impetuson.rexroot.view.jobreq

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.impetuson.rexroot.MainActivity
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentJobreqsubmissionsBinding
import com.impetuson.rexroot.model.jobreq.SubmissionsModelClass
import com.impetuson.rexroot.viewmodel.jobreq.SubmissionsRecyclerViewAdapter
import com.impetuson.rexroot.viewmodel.jobreq.SubmissionsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobreqSubmissionsFragment(jobId: String) : Fragment(), ResumeClickInterface {

    private var binding: FragmentJobreqsubmissionsBinding ?= null
    private lateinit var resumeList: List<List<SubmissionsModelClass>>
    private lateinit var submissionsAdapter: SubmissionsRecyclerViewAdapter
    val viewmodel = SubmissionsViewModel(jobId)
    private var currSpinnerPosition: Int = 0

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
            pbLoading.visibility = View.VISIBLE

            MainScope().launch {
                refreshSubmissions()

                spinnerStatus.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, viewmodel.items)
                spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                        viewmodel.onItemSelected(position)
                        currSpinnerPosition = position
                        submissionsAdapter = SubmissionsRecyclerViewAdapter(resumeList[position], this@JobreqSubmissionsFragment)
                        rvSubmissions.adapter = submissionsAdapter

                        binding!!.pbLoading.visibility = View.GONE
                        if (resumeList[position].isEmpty()){
                            tvNoresults.visibility = View.VISIBLE
                        } else {
                            tvNoresults.visibility = View.GONE
                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>) {}
                }
            }

            swipeToRefresh.setColorSchemeColors(resources.getColor(R.color.violet))

            swipeToRefresh.setOnRefreshListener {

                MainScope().launch {
                    refreshSubmissions()

                    spinnerStatus.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, viewmodel.items)
                    spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                            viewmodel.onItemSelected(position)
                            currSpinnerPosition = position
                            submissionsAdapter = SubmissionsRecyclerViewAdapter(resumeList[position], this@JobreqSubmissionsFragment)
                            rvSubmissions.adapter = submissionsAdapter

                            binding!!.pbLoading.visibility = View.GONE
                            if (resumeList[position].isEmpty()){
                                tvNoresults.visibility = View.VISIBLE
                            } else {
                                tvNoresults.visibility = View.GONE
                            }
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>) {}
                    }

                    swipeToRefresh.isRefreshing = false
                }
            }
        }

    }

    override fun onResumeClick(position: Int) {
        val filterBottomSheet = BottomSheetDialog(requireContext())
        filterBottomSheet.setContentView(R.layout.bottomsheet_resume)
        val filterBottomSheetBehavior = filterBottomSheet.behavior
        filterBottomSheetBehavior.isDraggable = true
        //filterBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        val resumeData = resumeList[currSpinnerPosition][position]

        filterBottomSheet.findViewById<TextView>(R.id.tv_resumename)!!.text = resumeData.resumename
        filterBottomSheet.findViewById<TextView>(R.id.tv_partnermsg)!!.text = resumeData.partnerMsg
        val tvResumeStatus: TextView = filterBottomSheet.findViewById(R.id.tv_resumestatus)!!
        val cvResumeStatus: CardView = filterBottomSheet.findViewById(R.id.cv_resumestatus)!!

        when (resumeData.resumestatus){
            "0" -> {
                tvResumeStatus.text = "ACTIVE"
                val color = ContextCompat.getColor(filterBottomSheet.context, R.color.orange)
                cvResumeStatus.backgroundTintList = ColorStateList.valueOf(color)
            }
            "1" -> {
                tvResumeStatus.text = "SELECTED"
                val color = ContextCompat.getColor(filterBottomSheet.context, R.color.primary_green)
                cvResumeStatus.backgroundTintList = ColorStateList.valueOf(color)
            }
            "-1" -> {
                tvResumeStatus.text = "REJECTED"
                val color = ContextCompat.getColor(filterBottomSheet.context, R.color.primary_red)
                cvResumeStatus.backgroundTintList = ColorStateList.valueOf(color)
            }
            else -> {
                tvResumeStatus.text = "ACTIVE"
                val color = ContextCompat.getColor(filterBottomSheet.context, R.color.orange)
                cvResumeStatus.backgroundTintList = ColorStateList.valueOf(color)
            }
        }
        filterBottomSheet.findViewById<TextView>(R.id.tv_resumepost)!!.text = resumeData.resumepost

        filterBottomSheet.show()
    }

    private suspend fun refreshSubmissions() = withContext(Dispatchers.IO){
        resumeList = viewmodel.fetchDataFromFirestore()
    }

}