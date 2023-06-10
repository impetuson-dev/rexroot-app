package com.impetuson.rexroot.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentLoginBinding
import com.impetuson.rexroot.viewmodel.ValidationViewModel

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private val formValidationViewModel: ValidationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login ,container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            loginFragment = this@LoginFragment
            validationViewModel = formValidationViewModel
        }

        // Exit app
        requireActivity().onBackPressedDispatcher.addCallback(this) {activity?.finish()}
    }

    fun redirectToSignUp(){
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }

    fun btnLogInHandler(){
        formValidationViewModel.setUserEmail(binding?.etEmail?.text.toString())
        formValidationViewModel.setUserPassword(binding?.etPassword?.text.toString())
        formValidationViewModel.formValidation()
    }

}