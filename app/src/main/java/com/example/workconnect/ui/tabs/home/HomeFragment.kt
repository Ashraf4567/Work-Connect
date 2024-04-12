package com.example.workconnect.ui.tabs.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.workconnect.R
import com.example.workconnect.data.model.Project
import com.example.workconnect.data.model.Task
import com.example.workconnect.databinding.FragmentHomeBinding
import com.example.workconnect.ui.auth.AuthViewModel
import com.example.workconnect.ui.tabs.projects.ProjectsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val projectsAdapter = ProjectsAdapter(emptyList())
    private val myTasksAdapter = MyTasksAdapter(null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        getCurrentUserTasks(viewModel.sessionManager.getUserData()?.name)

    }

    private fun getCurrentUserTasks(name: String?) {
        val db = FirebaseFirestore.getInstance() // Get Firestore instance

        val currentUserTasks = mutableListOf<Task>()

        db.collection(Project.PROJECTS_COLLECTION_NAME) // Reference projects collection
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val project = document.toObject(Project::class.java) // Convert document to Project object
                    if (project?.tasks != null) {
                        // Iterate through the tasks of the project
                        for (task in project.tasks) {
                            // Check if the task's workerName matches the current user's name
                            if (task?.workerName == name) {
                                if (task != null) {
                                    currentUserTasks.add(task)
                                }
                            }
                        }
                    } else {
                        Log.w("GetCurrentUserTasks", "Error converting document to Project object or tasks are null")
                    }
                }

                myTasksAdapter.submitList(currentUserTasks)
                // Handle currentUserTasks (e.g., display or perform actions)
            }
            .addOnFailureListener { exception ->
                Log.w("GetCurrentUserTasks", "Error getting projects:", exception)
                // Handle failure (e.g., show error message)
            }
    }


    private fun initViews() {
        binding.projectsRecycler.adapter = projectsAdapter
        binding.myTasksRecycler.adapter = myTasksAdapter


        getProjectsList()
    }

    private fun getProjectsList() {
        val db = FirebaseFirestore.getInstance() // Get Firestore instance

        db.collection(Project.PROJECTS_COLLECTION_NAME) // Reference projects collection
            .get()
            .addOnSuccessListener { result ->
                val projects = mutableListOf<Project>() // List to store retrieved projects
                for (document in result.documents) {
                    val project = document.toObject(Project::class.java) // Convert document to Project object
                    if (project != null) {
                        projects.add(project)
                    } else {
                        Log.w("GetProjectsList", "Error converting document to Project object")
                    }

                }

                projectsAdapter.submitList(projects)
                // Handle retrieved projects (e.g., update UI, perform actions)
            }
            .addOnFailureListener { exception ->
                Log.w("GetProjectsList", "Error getting projects:", exception)
                // Handle failure (e.g., show error message)
            }
    }


}