package com.example.workconnect.ui.tabs.projects

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.workconnect.R
import com.example.workconnect.data.model.Project
import com.example.workconnect.data.model.Task
import com.example.workconnect.data.model.TaskCompletionState
import com.example.workconnect.databinding.FragmentAddTaskBinding
import com.example.workconnect.databinding.FragmentLoginBinding
import com.example.workconnect.ui.auth.AuthViewModel
import com.example.workconnect.utils.Constants.Companion.PROJECT_ID
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AddTaskFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddTaskBinding

    private val viewModel: AuthViewModel by viewModels()
    var projectId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        projectId = arguments?.getString(PROJECT_ID).toString()

        binding.createTaskBtn.setOnClickListener {
            addTask()
        }
    }

    private fun addTask() {
        val firestore = FirebaseFirestore.getInstance()
        val projectsRef = firestore.collection(Project.PROJECTS_COLLECTION_NAME)

        // Retrieve the project by its ID
        projectsRef.whereEqualTo("id", projectId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Convert document snapshot to Project object
                    val project = document.toObject(Project::class.java)

                    // Create a new task
                    val task = Task(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        title = binding.tasksTitle.text.toString(),
                        description = binding.taskDescription.text.toString(),
                        taskCompletionState = TaskCompletionState.NEW.state,
                        creatorName = viewModel.sessionManager.getUserData()?.name,
                        points = binding.taskPoins.text.toString()
                    )

                    // Add the new task to the project's task list
                    val updatedTaskList = project.tasks.orEmpty().toMutableList()
                    updatedTaskList.add(task)

                    // Update the project with the new task list
                    val updatedProject = project.copy(tasks = updatedTaskList.toList())

                    // Save the updated project back to Firestore
                    projectsRef.document(document.id)
                        .set(updatedProject)
                        .addOnSuccessListener {
                            // Task added successfully
                            Log.d("AddTaskFragment", "Task added successfully")
                            dismiss() // Dismiss the bottom sheet dialog
                        }
                        .addOnFailureListener { exception ->
                            // Error adding task
                            Log.e("AddTaskFragment", "Error adding task", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Error retrieving project
                Log.e("AddTaskFragment", "Error retrieving project", exception)
            }
    }


}