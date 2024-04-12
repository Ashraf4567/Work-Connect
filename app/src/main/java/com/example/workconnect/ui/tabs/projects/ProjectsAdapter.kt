package com.example.workconnect.ui.tabs.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workconnect.R
import com.example.workconnect.controlPanel.EmployeesAdapter.ViewHolder
import com.example.workconnect.data.model.Project
import com.example.workconnect.databinding.ItemEmployeeBinding
import com.example.workconnect.databinding.ItemProjectBinding

class ProjectsAdapter(var projectsList: List<Project?>): RecyclerView.Adapter<ProjectsAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemProjectBinding): RecyclerView.ViewHolder(item.root){

        fun bind(project: Project){
            item.project = project
            var bg = R.drawable.project_bg1
            if (project.background == 1){
                bg = R.drawable.project_bg1
                item.projectBg.setImageResource(bg)
            }
            if (project.background == 2){
                bg = R.drawable.project_bg2
                item.projectBg.setImageResource(bg)
            }
            if (project.background == 3){
                bg = R.drawable.project_bg3
                item.projectBg.setImageResource(bg)
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemProjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projectsList[position]
        holder.bind(project!!)

        onProjectClickListener.let {
            holder.item.root.setOnClickListener {
                onProjectClickListener?.onProjectClick(project.id!!)
            }
        }
    }

    var onProjectClickListener: OnProjectClickListener? = null
    fun interface OnProjectClickListener{
        fun onProjectClick(id: String)
    }

    fun submitList(list: List<Project>){
        projectsList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = projectsList.size
}