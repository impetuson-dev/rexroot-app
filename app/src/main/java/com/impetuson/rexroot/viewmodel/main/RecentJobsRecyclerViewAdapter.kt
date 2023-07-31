package com.impetuson.rexroot.viewmodel.main

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.impetuson.rexroot.JobreqActivity
import com.impetuson.rexroot.R
import com.impetuson.rexroot.model.jobreq.JobReqModelClass

class RecentJobsRecyclerViewAdapter(private var dataList: List<JobReqModelClass>) : RecyclerView.Adapter<RecentJobsRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val jobRole: TextView = itemView.findViewById(R.id.tv_jobrole)
        private val companyName: TextView = itemView.findViewById(R.id.tv_compname)
        private val companyLocation: TextView = itemView.findViewById(R.id.tv_complocation)
        private val payment: TextView = itemView.findViewById(R.id.tv_payment)
        private val candpayment: TextView = itemView.findViewById(R.id.tv_candpayment)

        fun bind(data: JobReqModelClass) {
            jobRole.text = data.jobrole + " - " + data.jobsalary
            companyName.text = data.compname
            companyLocation.text = data.complocation
            payment.text = "₹" + data.priceperclosure
            candpayment.text = "₹" + (((data.priceperclosure).toInt())/2)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, JobreqActivity::class.java)
                val extras = Bundle().apply {
                    putString("jobid", data.jobid)
                    putString("jobRole", data.jobrole)
                    putString("reqJobExp", data.reqjobexp)
                    putString("jobSalary", data.jobsalary)
                }
                intent.putExtras(extras)
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