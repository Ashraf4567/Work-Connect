package com.example.workconnect.ui.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workconnect.data.model.AttendanceHistory
import com.example.workconnect.data.model.User
import com.example.workconnect.databinding.ItemAttendanceBinding
import com.example.workconnect.databinding.ItemEmployeeBinding


class AttendanceHistoryAdapter(var historyList: MutableList<AttendanceHistory?>?) :
    RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemAttendanceBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(attendanceHistory: AttendanceHistory) {
            item.attendance = attendanceHistory
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemAttendanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
    }

    override fun getItemCount(): Int = historyList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attendanceHistory = historyList!![position]
        holder.bind(attendanceHistory!!)

    }



    fun submitList(list: List<AttendanceHistory?>?) {
        historyList = list as MutableList<AttendanceHistory?>?
        notifyDataSetChanged()
    }

}