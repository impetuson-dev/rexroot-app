package com.impetuson.rexroot.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentCandidateBinding
import com.impetuson.rexroot.databinding.FragmentPartnerBinding
import com.impetuson.rexroot.viewmodel.profile.CandidateViewModel
import com.impetuson.rexroot.viewmodel.profile.PartnerViewModel

class CandidateFragment : Fragment() {

    private var binding : FragmentCandidateBinding? = null
    private val viewmodel: CandidateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_candidate ,container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            candidateFragment = this@CandidateFragment
            candidateViewModel = viewmodel

            cvGoback.setOnClickListener {
                activity?.onBackPressed()
            }
        }

    }

}