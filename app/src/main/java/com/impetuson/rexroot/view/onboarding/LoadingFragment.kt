package com.impetuson.rexroot.view.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.MainActivity
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentLoadingBinding


class LoadingFragment : Fragment() {

    private val TIME_OUT: Long = 5000
    private var binding: FragmentLoadingBinding?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentLoadingBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        Handler().postDelayed({
            startActivity(Intent(requireContext(),MainActivity::class.java)) }
            , TIME_OUT)
    }

}