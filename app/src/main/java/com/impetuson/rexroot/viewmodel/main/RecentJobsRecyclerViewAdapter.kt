package com.impetuson.rexroot.viewmodel.main

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.impetuson.rexroot.JobreqActivity
import com.impetuson.rexroot.R
import com.impetuson.rexroot.model.jobreq.JobReqModelClass

class RecentJobsRecyclerViewAdapter(private val dataList: List<JobReqModelClass>) : RecyclerView.Adapter<RecentJobsRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val jobRole: TextView = itemView.findViewById(R.id.tv_jobrole)
        private val companyName: TextView = itemView.findViewById(R.id.tv_compname)
        private val companyLocation: TextView = itemView.findViewById(R.id.tv_complocation)
        private val payment: TextView = itemView.findViewById(R.id.tv_payment)

        fun bind(data: JobReqModelClass) {
            jobRole.text = data.jobrole
            companyName.text = data.compname
            companyLocation.text = data.complocation
            payment.text = "₹" + data.priceperclosure

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, JobreqActivity::class.java)
                intent.putExtra("jobid", data.jobid)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recentjob_card, parent, false)
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