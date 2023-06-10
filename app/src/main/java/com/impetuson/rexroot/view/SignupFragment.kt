package com.impetuson.rexroot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentSignupBinding
import com.impetuson.rexroot.viewmodel.SignupViewModel

class SignupFragment : Fragment() {

    private var binding: FragmentSignupBinding? = null
    private val viewmodel: SignupViewModel by activityViewModels()

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

        viewmodel.resetViewModel()

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            binding?.signupFragment = this@SignupFragment
            signupViewModel = viewmodel
        }
    }

    fun redirectToLogIn(){
        findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
    }

    fun btnSignUpHandler(){
        val validation: Boolean = viewmodel.signupFormValidation()
        if (validation){
            Toast.makeText(context,"Signup Successful",Toast.LENGTH_LONG).show()
        }
    }



}