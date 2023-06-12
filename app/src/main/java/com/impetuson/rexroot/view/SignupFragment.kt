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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
            MainScope().launch {
                binding!!.progressBar.visibility = View.VISIBLE
                val (authStatus,authMsg) = viewmodel.signupAuthentication()
                Toast.makeText(context,"$authMsg",Toast.LENGTH_LONG).show()
                if (authStatus as Boolean){
                    viewmodel.storeDatatoFirestore()
                    findNavController().navigate(R.id.action_signupFragment_to_userdetailsFragment)
                }
                binding!!.progressBar.visibility = View.GONE
            }
        }
    }
}