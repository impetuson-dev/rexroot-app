package com.impetuson.rexroot.view.profile

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.ActivityPartnerBinding
import com.impetuson.rexroot.viewmodel.profile.PartnerViewModel

class PartnerActivity: AppCompatActivity() {

    private lateinit var binding : ActivityPartnerBinding
    private var viewmodel = PartnerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPartnerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        changeStatusBarColor()

        binding.apply {
            lifecycleOwner = this@PartnerActivity
            partnerViewModel = viewmodel

            cvGoback.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun changeStatusBarColor() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_red)
    }
}