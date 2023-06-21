package com.impetuson.rexroot

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.rpc.context.AttributeContext.Resource
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
                if (formValidation()){
                    val intent = Intent(this@SearchActivity,JobsActivity::class.java)
                    intent.putExtra("jobsearch",etJobsearch.text.toString())
                    intent.putExtra("location",etLocation.text.toString())
                    startActivity(intent)
                }
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

    private fun formValidation(): Boolean{
        if (binding.etJobsearch.text.isEmpty() && binding.etLocation.text.isEmpty()){
            binding.etJobsearch.hint = "Type here to search"
            binding.etLocation.hint = "Type here to search"
            binding.tvHint1.visibility = View.VISIBLE
            binding.tvHint2.visibility = View.VISIBLE
            binding.tvHint1.setTextColor(ContextCompat.getColor(this, R.color.primary_red))
            binding.tvHint2.setTextColor(ContextCompat.getColor(this, R.color.primary_red))
            return false
        }
        binding.tvHint1.setTextColor(ContextCompat.getColor(this, R.color.violet))
        binding.tvHint2.setTextColor(ContextCompat.getColor(this, R.color.violet))
        return true
    }
}