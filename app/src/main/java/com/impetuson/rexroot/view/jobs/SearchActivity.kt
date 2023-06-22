package com.impetuson.rexroot.view.jobs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.impetuson.rexroot.R
import com.impetuson.rexroot.databinding.ActivitySearchBinding
import com.impetuson.rexroot.model.jobs.SearchModelClass
import com.impetuson.rexroot.viewmodel.jobs.SearchRecyclerViewAdapter

class SearchActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchesAdapter: SearchRecyclerViewAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private var searchesList: List<SearchModelClass> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = this.getSharedPreferences("recentsearches", Context.MODE_PRIVATE)

        if (searchesList.isEmpty()){ searchesList = getSearchList("searchesList") }

        binding.apply {
            lifecycleOwner = this@SearchActivity

            if (searchesList.isEmpty()){ tvNoresults.visibility = View.VISIBLE }

            rvRecentsearches.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
            searchesAdapter = SearchRecyclerViewAdapter(searchesList)
            rvRecentsearches.adapter = searchesAdapter

            btnSearch.setOnClickListener {
                if (formValidation()){
                    saveSearchList("searchesList", SearchModelClass(etJobsearch.text.toString(),etLocation.text.toString()))

                    val intent = Intent(this@SearchActivity, JobsActivity::class.java)
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

    private fun saveSearchList(key: String, search: SearchModelClass) {
        val searches = getSearchList(key).toMutableList()
        searches.add(0,search)
        if (searches.size > 10){ searches.removeLast() }

        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(searches)
        editor.putString(key, json)
        editor.apply()
    }

    private fun getSearchList(key: String): List<SearchModelClass> {
        val gson = Gson()
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<List<SearchModelClass>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}