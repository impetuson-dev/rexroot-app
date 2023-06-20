package com.impetuson.rexroot.view.profile

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.ActivityCandidateBinding
import com.impetuson.rexroot.viewmodel.profile.CandidateViewModel

class CandidateActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCandidateBinding
    private var viewmodel = CandidateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCandidateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        changeStatusBarColor()

        binding.apply {
            lifecycleOwner = this@CandidateActivity
            candidateViewModel = viewmodel

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