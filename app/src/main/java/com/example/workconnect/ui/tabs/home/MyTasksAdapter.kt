package com.example.workconnect.ui.tabs.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workconnect.data.model.Task
import com.example.workconnect.data.model.TaskCompletionState
import com.example.workconnect.databinding.ItemMyTasksBinding


class MyTasksAdapter(var tasksList: MutableList<Task?>?) :
    RecyclerView.Adapter<MyTasksAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemMyTasksBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(task: Task) {
            item.task = task

            if (task.taskCompletionState.equals(TaskCompletionState.IN_PROGRESS.state)) {
                item.checkbox.isChecked = false
            }
            if (task.taskCompletionState.equals(TaskCompletionState.COMPLETED.state)) {
                item.checkbox.isChecked = true
            }

            item.executePendingBindings()
            
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemMyTasksBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
    }

    override fun getItemCount(): Int = tasksList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasksList?.get(position)
        holder.bind(task!!)
        onTakeTaskCheckedListener.let {
            holder.item.checkbox.setOnClickListener {
                onTakeTaskCheckedListener?.onTaskChecked(task, position)
            }
        }


    }

    var onTakeTaskCheckedListener: OnCheckClickListener? = null


    fun interface OnCheckClickListener {
        fun onTaskChecked(task: Task, position: Int)
    }

    fun submitList(list: List<Task?>?) {
        tasksList = list as MutableList<Task?>?
        notifyDataSetChanged()
    }

}