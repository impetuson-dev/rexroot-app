package com.impetuson.rexroot.viewmodel.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.impetuson.rexroot.R
import com.impetuson.rexroot.model.profile.JobReqModelClass
class JobReqRecyclerViewAdapter(private val dataList: List<JobReqModelClass>) : RecyclerView.Adapter<JobReqRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val jobRole: TextView = itemView.findViewById(R.id.tv_jobrole)
        private val companyName: TextView = itemView.findViewById(R.id.tv_compname)

        fun bind(data: JobReqModelClass) {
            jobRole.text = data.jobrole
            companyName.text = data.compname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.jobreq_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}