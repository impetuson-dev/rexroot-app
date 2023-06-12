package com.impetuson.rexroot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentSignupBinding
import com.impetuson.rexroot.databinding.FragmentUserdetailsBinding
import com.impetuson.rexroot.viewmodel.SignupViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class UserdetailsFragment : Fragment() {

    private var binding: FragmentUserdetailsBinding ?= null
    private val signupviewmodel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentUserdetailsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            userdetailsFragment = this@UserdetailsFragment
            signupViewModel = signupviewmodel
        }
    }

    fun btnContinueHandler() {
        MainScope().launch {
            binding!!.progressBar.visibility = View.VISIBLE
            findNavController().navigate(R.id.action_userdetailsFragment_to_homeFragment)
            binding!!.progressBar.visibility = View.GONE
        }
    }
}