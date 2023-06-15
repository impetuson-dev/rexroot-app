package com.impetuson.rexroot.view.onboarding


import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.FragmentUserdetailsBinding
import com.impetuson.rexroot.viewmodel.onboarding.SignupViewModel
import com.impetuson.rexroot.viewmodel.onboarding.UserdetailsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class UserdetailsFragment : Fragment() {

    private var binding: FragmentUserdetailsBinding ?= null
    private val signupviewmodel: SignupViewModel by activityViewModels()
    private val userdetailsviewmodel: UserdetailsViewModel by activityViewModels()

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
            userdetailsViewModel = userdetailsviewmodel
        }
    }

    fun btnContinueHandler() {
        MainScope().launch {
            binding!!.progressBar.visibility = View.VISIBLE
            val (storeStatus,storeMsg) = userdetailsviewmodel.storeDataToFirestore()
            Toast.makeText(context,"$storeMsg", Toast.LENGTH_LONG).show()
            if (storeStatus as Boolean){
                findNavController().navigate(R.id.action_userdetailsFragment_to_loadingFragment)
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    fun btnSelectDateHandler() {
        val currDate = userdetailsviewmodel.currentDate()
        val datePickerDialog = DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->

            Log.d("year","$year")
            Log.d("month","$monthOfYear") // Starts from index 0
            Log.d("date","$dayOfMonth")

            userdetailsviewmodel.setdobDay(dayOfMonth)
            userdetailsviewmodel.setdobMonth(monthOfYear+1)
            userdetailsviewmodel.setdobYear(year)
            userdetailsviewmodel.setDob() },
            currDate[2],
            currDate[1],
            currDate[0])

        datePickerDialog.show()
    }

}