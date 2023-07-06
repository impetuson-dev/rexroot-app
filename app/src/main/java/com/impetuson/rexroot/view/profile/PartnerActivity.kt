package com.impetuson.rexroot.view.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.ActivityPartnerBinding
import com.impetuson.rexroot.model.profile.PartnerModelClass
import com.impetuson.rexroot.viewmodel.profile.PartnerViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PartnerActivity: AppCompatActivity() {

    private lateinit var binding : ActivityPartnerBinding
    private var viewmodel = PartnerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor()
        binding = ActivityPartnerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val userId = intent.getStringExtra("userid")
        Log.d("Partner profile","User ID: $userId")
        viewmodel._userId.value = userId.toString()

        binding.progressBar.visibility = View.VISIBLE

        MainScope().launch {
            val (fetchStatus, partnerData) = viewmodel.fetchDataFromFirestore()

            if (fetchStatus as Boolean) {
                viewmodel.setDetails(partnerData as PartnerModelClass)
            } else {
                Toast.makeText(this@PartnerActivity, "Error occurred", Toast.LENGTH_SHORT).show()
            }

            binding.progressBar.visibility = View.GONE

            binding.apply {
                lifecycleOwner = this@PartnerActivity
                partnerViewModel = viewmodel

                cvGoback.setOnClickListener {
                    onBackPressed()
                }


            }
        }
    }

    private fun changeStatusBarColor(){
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.violet)
    }

}