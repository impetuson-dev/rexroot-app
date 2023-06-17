package com.impetuson.rexroot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class JobreqActivity: AppCompatActivity() {

    private lateinit var jobId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobreq)

        jobId = intent.getStringExtra("jobId") ?: ""



    }
}