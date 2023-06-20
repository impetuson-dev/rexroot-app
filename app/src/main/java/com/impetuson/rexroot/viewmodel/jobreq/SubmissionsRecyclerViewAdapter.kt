package com.impetuson.rexroot.viewmodel.jobreq

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.impetuson.rexroot.R
import com.impetuson.rexroot.model.jobreq.SubmissionsModelClass

class SubmissionsRecyclerViewAdapter(private val dataList: List<SubmissionsModelClass>): RecyclerView.Adapter<SubmissionsRecyclerViewAdapter.ViewHolder>() {
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
        private val resumePost: TextView = itemView.findViewById(R.id.tv_resumepost)
        private val resumeStatus: TextView = itemView.findViewById(R.id.tv_resumestatus)
        private val cvResumeStatus: CardView = itemView.findViewById(R.id.cv_resumestatus)
        fun bind(data: SubmissionsModelClass) {
            resumeName.text = data.resumename
            resumePost.text = "Submitted on: " + data.resumepost
            when (data.resumestatus){
                "0" -> {
                    resumeStatus.text = "ACTIVE"
                    val color = ContextCompat.getColor(itemView.context, R.color.orange)
                    cvResumeStatus.backgroundTintList = ColorStateList.valueOf(color)
                }
                "1" -> {
                    resumeStatus.text = "SELECT"
                    val color = ContextCompat.getColor(itemView.context, R.color.primary_green)
                    cvResumeStatus.backgroundTintList = ColorStateList.valueOf(color)
                }
                "-1" -> {
                    resumeStatus.text = "REJECT"
                    val color = ContextCompat.getColor(itemView.context, R.color.primary_red)
                    cvResumeStatus.backgroundTintList = ColorStateList.valueOf(color)
                }
                else -> {
                    resumeStatus.text = "ACTIVE"
                    val color = ContextCompat.getColor(itemView.context, R.color.orange)
                    cvResumeStatus.backgroundTintList = ColorStateList.valueOf(color)
                }
            }
        }
    }
}