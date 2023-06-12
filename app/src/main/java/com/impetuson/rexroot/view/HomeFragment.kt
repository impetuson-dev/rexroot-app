package com.impetuson.rexroot.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.Profile
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.MainActivity
import com.impetuson.rexroot.ProfileActivity
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

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
        }

        // Exit app
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }

        binding!!.btnMyprofile.setOnClickListener {
            val intent = Intent(context,ProfileActivity::class.java)
            startActivity(intent)
            //findNavController().navigate(R.id.action_homeFragment_to_myProfileFragment)
        }
    }

    private fun changeStatusBarColor() {
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.WHITE
    }

}