package com.impetuson.rexroot.view.profile

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentPartnerBinding
import com.impetuson.rexroot.databinding.FragmentSignupBinding
import com.impetuson.rexroot.viewmodel.profile.PartnerViewModel


class PartnerFragment : Fragment() {

    private var binding : FragmentPartnerBinding? = null
    private val viewmodel: PartnerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentPartnerBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewmodel.fetchDetails()
        changeStatusBarColor()

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            partnerViewModel = viewmodel

            cvGoback.setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }

    private fun changeStatusBarColor() {
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_red)
    }

}