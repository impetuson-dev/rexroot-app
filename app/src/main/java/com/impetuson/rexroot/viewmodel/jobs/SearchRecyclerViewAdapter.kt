package com.impetuson.rexroot.viewmodel.jobs

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.impetuson.rexroot.R
import com.impetuson.rexroot.model.jobs.SearchModelClass
import com.impetuson.rexroot.view.jobs.JobsActivity

class SearchRecyclerViewAdapter(private var searches: List<SearchModelClass>): RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>() {

    fun updateSearchesList(updatedSearches: List<SearchModelClass>){
        searches = updatedSearches
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.searches_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchRecyclerViewAdapter.ViewHolder, position: Int) {
        val data = searches[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return searches.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val tvSearch: TextView = itemView.findViewById(R.id.tv_search)
        fun bind(data: SearchModelClass){
            tvSearch.text = data.jobsearch + ", " + data.location

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, JobsActivity::class.java)
                intent.putExtra("jobsearch",data.jobsearch.trim())
                intent.putExtra("location",data.location.trim())
                itemView.context.startActivity(intent)
            }
        }

    }

}