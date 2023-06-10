package com.impetuson.rexroot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentLoginBinding
import com.impetuson.rexroot.viewmodel.LoginViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private val viewmodel: LoginViewModel by activityViewModels()

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

        viewmodel.resetViewModel()

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            loginFragment = this@LoginFragment
            loginViewModel = viewmodel
        }

        // Exit app
        requireActivity().onBackPressedDispatcher.addCallback(this) {activity?.finish()}
    }

    fun redirectToSignUp(){
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }

    fun btnLogInHandler(){
        val validation: Boolean = viewmodel.loginFormValidation()
        if (validation){
            MainScope().launch {
                binding!!.progressBar.visibility = View.VISIBLE
                val (authStatus,authMsg) = viewmodel.loginAuthentication()
                Toast.makeText(context,"$authMsg",Toast.LENGTH_LONG).show()
                if (authStatus as Boolean){
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                binding!!.progressBar.visibility = View.GONE
            }
        }
    }

}