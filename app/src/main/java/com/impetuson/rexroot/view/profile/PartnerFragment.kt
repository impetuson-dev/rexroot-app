package com.impetuson.rexroot.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentPartnerBinding
import com.impetuson.rexroot.viewmodel.profile.PartnerViewModel


class PartnerFragment : Fragment() {

    private var binding : FragmentPartnerBinding? = null
    private val viewmodel: PartnerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_partner ,container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            partnerFragment = this@PartnerFragment
            partnerViewModel = viewmodel

            cvGoback.setOnClickListener {
                activity?.onBackPressed()
            }
        }

    }

}