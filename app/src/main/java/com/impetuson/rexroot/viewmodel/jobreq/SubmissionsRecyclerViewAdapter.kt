package com.impetuson.rexroot.viewmodel.jobreq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.impetuson.rexroot.R

class SubmissionsRecyclerViewAdapter(private val dataList: List<String>): RecyclerView.Adapter<SubmissionsRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubmissionsRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.submission_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SubmissionsRecyclerViewAdapter.ViewHolder,
        position: Int
    ) {
        val data = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val resumeName: TextView = itemView.findViewById(R.id.tv_resumename)
        fun bind(data: String) {
            resumeName.text = data
        }
    }
}