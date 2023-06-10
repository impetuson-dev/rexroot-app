package com.impetuson.rexroot.view

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {

    private var binding: FragmentSignupBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSignupBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            binding?.signupFragment = this@SignupFragment
        }
    }

    fun redirectToLogIn(){
        findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
    }



}