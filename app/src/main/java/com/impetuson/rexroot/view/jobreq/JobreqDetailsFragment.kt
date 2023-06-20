package com.impetuson.rexroot.view.jobreq

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentJobreqdetailsBinding
import com.impetuson.rexroot.viewmodel.jobreq.JobreqViewModel
import com.impetuson.rexroot.viewmodel.main.HomeViewModel


class JobreqDetailsFragment : Fragment() {

    private var binding: FragmentJobreqdetailsBinding? = null
    private val viewmodel: JobreqViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jobreqdetails, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            jobreqviewmodel = viewmodel

        }
    }

}