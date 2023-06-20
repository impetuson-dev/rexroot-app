package com.impetuson.rexroot.view.onboarding

import android.content.Context
import android.content.Intent
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
import com.impetuson.rexroot.MainActivity
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentLoginBinding
import com.impetuson.rexroot.viewmodel.onboarding.LoginViewModel
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
        requireActivity().onBackPressedDispatcher.addCallback(this) {activity?.finishAffinity()}
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
                val fetchStatus = viewmodel.fetchUserDataFromFirestore()

                val sharedPreferences = requireContext().getSharedPreferences("profiledata",Context.MODE_PRIVATE)
                viewmodel.storeDataToSharedPreferences(sharedPreferences)

                Toast.makeText(context,"$authMsg",Toast.LENGTH_SHORT).show()
                if (authStatus as Boolean){
                    startActivity(Intent(requireContext(),MainActivity::class.java))
                }
                binding!!.progressBar.visibility = View.GONE
            }
        }
    }
}