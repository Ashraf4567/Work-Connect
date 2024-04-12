package com.example.workconnect.ui.tabs.projects

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.workconnect.R
import com.example.workconnect.data.model.Project
import com.example.workconnect.data.model.Project.Companion.PROJECTS_COLLECTION_NAME
import com.example.workconnect.data.model.TaskCompletionState
import com.example.workconnect.databinding.FragmentProjectDetailsBinding
import com.example.workconnect.ui.auth.AuthViewModel
import com.example.workconnect.utils.Constants
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectDetailsFragment : Fragment() {

    lateinit var binding: FragmentProjectDetailsBinding
    private val adapter = AllTasksAdapter(null)
    private val viewModel: AuthViewModel by viewModels()

    val tabsList = listOf("New" , "In Progress" , "Completed")
    var projectId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProjectDetailsBinding.inflate(layoutInflater , container , false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        projectId = arguments?.getString(Constants.PROJECT_ID).toString()
        Log.d("test projectId" , projectId?:"failed")
        initViews()

        getProjectDataById()
    }

    private fun initViews() {
        binding.tasksRecycler.adapter = adapter

        binding.addTaskBtn.setOnClickListener {
            navigateToAddTask()
        }

        adapter.onTakeTaskClickListener = AllTasksAdapter.OnTaskClickListener { task, position ->
            val taskId = task.id

            takeTask(taskId , viewModel.sessionManager.getUserData()?.name.toString())
        }

        bindTabs()

        val swipeToRefreshView = binding.swiperefresh
        swipeToRefreshView.setOnRefreshListener {
            getProjectDataById()
            swipeToRefreshView.isRefreshing = false
        }
    }

    private fun takeTask(taskId: String?, workerName: String?) {
        binding.progressBar.visibility = View.VISIBLE
        val firestore = FirebaseFirestore.getInstance()
        val projectsRef = firestore.collection(PROJECTS_COLLECTION_NAME)

        // Retrieve the project by its ID
        projectsRef.whereEqualTo("id", projectId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Convert document snapshot to Project object
                    val project = document.toObject(Project::class.java)

                    // Find the task with the specified ID
                    val updatedTasks = project.tasks?.map { task ->
                        if (task?.id == taskId) {
                            // Update the task's completion state and worker name
                            task?.copy(
                                taskCompletionState = TaskCompletionState.IN_PROGRESS.state,
                                workerName = workerName
                            )
                        } else {
                            task
                        }
                    }

                    // Update the project with the updated tasks list
                    val updatedProject = project.copy(tasks = updatedTasks)

                    // Save the updated project back to Firestore
                    projectsRef.document(document.id)
                        .set(updatedProject)
                        .addOnSuccessListener {
                            // Task taken successfully
                            Log.d("ProjectDetailsFragment", "Task taken successfully")
                            Toast.makeText(requireActivity() , "Task Taken successfully" , Toast.LENGTH_LONG).show()
                            getProjectDataById()
                            binding.progressBar.visibility = View.GONE
                        }
                        .addOnFailureListener { exception ->
                            // Error taking task
                            Log.e("ProjectDetailsFragment", "Error taking task", exception)
                            binding.progressBar.visibility = View.GONE
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Error retrieving project
                Log.e("ProjectDetailsFragment", "Error retrieving project", exception)
            }
    }


    private fun navigateToAddTask() {
        val bundle = Bundle()
        bundle.putString(Constants.PROJECT_ID , projectId)
        findNavController().navigate(R.id.action_projectDetailsFragment_to_addTaskFragment , bundle)
    }






    var selectedTabIndex: Int = 0
    var selectedTabName: String = ""
    private fun bindTabs() {

        tabsList.forEach { categoryName ->
            val tab = binding.tapLayout.newTab()
            tab.text = categoryName
            binding.tapLayout.addTab(tab)
            var layoutParams = LinearLayout.LayoutParams(tab.view.layoutParams)
            layoutParams.marginEnd = 50
            layoutParams.topMargin = 15
            layoutParams.marginStart= 58
            tab.view.layoutParams = layoutParams
        }

        binding.tapLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    selectedTabName = tab?.text.toString()
                    Log.d("tas tab", selectedTabName)
                    //viewModel.getAllTasksByCategory(selectedTabName)
                    getProjectDataById()
                    selectedTabIndex = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    selectedTabName = tab?.text.toString()
                    //viewModel.getAllTasksByCategory(selectedTabName)
                    getProjectDataById()
                }

            }
        )
        binding.tapLayout.getTabAt(0)?.select()
    }

    private fun getProjectDataById() {
        val firestore = FirebaseFirestore.getInstance()
        val projectsRef = firestore.collection(PROJECTS_COLLECTION_NAME)

        // Execute query to get project by ID
        projectsRef.whereEqualTo("id", projectId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Convert document snapshot to Project object
                    val project = document.toObject(Project::class.java)
                    Log.d("test get project" , project.title.toString())
                    bindProjectData(project)
                    // Process the project data
                    // For example, you can access project properties like project.id, project.title, etc.
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    private fun bindProjectData(project: Project) {
        binding.projectTitle.text = project.title
        binding.projectDescription.text = project.description

        var bg = R.drawable.project_bg1
        if (project.background == 1){
            bg = R.drawable.project_bg1
        }
        if (project.background == 2){
            bg = R.drawable.project_bg2
        }
        if (project.background == 3){
            bg = R.drawable.project_bg3
        }

        binding.projectImg.setImageResource(bg)

        val filtered = project.tasks?.filter { task ->
            task?.taskCompletionState == selectedTabName
        }
        adapter.submitList(filtered)
    }
}
