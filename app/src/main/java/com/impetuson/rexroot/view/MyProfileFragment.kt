package com.impetuson.rexroot.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
        changeStatusBarColor()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile ,container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
        }


        binding!!.cvGoback.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_homeFragment)
        }
    }

    private fun changeStatusBarColor() {
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#E51E26")
    }

}