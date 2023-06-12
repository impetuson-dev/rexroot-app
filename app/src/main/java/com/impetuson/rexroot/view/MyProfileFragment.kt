package com.impetuson.rexroot.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentMyProfileBinding


class MyProfileFragment : Fragment() {

    private var binding: FragmentMyProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile ,container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeStatusBarColor()

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
        }


        binding!!.cvGoback.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_homeFragment)
        }
    }

    private fun changeStatusBarColor() {
        val window = requireActivity().window
        if (requireActivity().supportFragmentManager.findFragmentById(R.id.myProfileFragment) is MyProfileFragment) {
            // Set the desired status bar color
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_red)
        } else {
            // Reset the status bar color to the default color
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        }
    }

}