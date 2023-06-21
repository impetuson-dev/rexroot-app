package com.impetuson.rexroot

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.impetuson.rexroot.databinding.ActivitySearchBinding

class SearchActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.apply {
            lifecycleOwner = this@SearchActivity

            btnSearch.setOnClickListener {
                val intent = Intent(this@SearchActivity,JobsActivity::class.java)
                intent.putExtra("jobsearch",etJobsearch.text.toString())
                intent.putExtra("location",etLocation.text.toString())
                startActivity(intent)
            }

            ivGoback.setOnClickListener {
                onBackPressed()
            }

            etJobsearch.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) { tvHint1.visibility = View.VISIBLE }
                else { if (etJobsearch.text.isEmpty()){ tvHint1.visibility = View.INVISIBLE } }
            }

            etLocation.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) { tvHint2.visibility = View.VISIBLE }
                else { if (etLocation.text.isEmpty()){ tvHint2.visibility = View.INVISIBLE } }
            }
        }
    }
}