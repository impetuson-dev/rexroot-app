package com.impetuson.rexroot.view.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.OnboardingActivity
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentMyProfileBinding
import com.impetuson.rexroot.viewmodel.profile.MyProfileViewModel

class MyProfileFragment : Fragment() {

    private var binding: FragmentMyProfileBinding? = null
    private val viewmodel: MyProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changeStatusBarColor()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile ,container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewmodel

            val sharedPreference: SharedPreferences  =  requireContext().getSharedPreferences("profiledata", Context.MODE_PRIVATE)
            viewmodel.getUserProfileDetails(sharedPreference)

            cvGoback.setOnClickListener {
                activity?.onBackPressed()
            }

            cvPartner.setOnClickListener {
                findNavController().navigate(R.id.action_myProfileFragment_to_partnerFragment)
            }

            cvCandidate.setOnClickListener {
                findNavController().navigate(R.id.action_myProfileFragment_to_candidateFragment)
            }

            btnLogout.setOnClickListener {
                viewmodel.btnLogOutHandler()
                startActivity(Intent(requireActivity(),OnboardingActivity::class.java))
            }
        }

    }

    private fun changeStatusBarColor() {
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#E51E26")
    }

}