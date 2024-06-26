package com.example.workconnect.ui.tabs.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.workconnect.data.model.Project
import com.example.workconnect.data.model.Task
import com.example.workconnect.data.model.TaskCompletionState
import com.example.workconnect.data.model.User
import com.example.workconnect.databinding.FragmentHomeBinding
import com.example.workconnect.ui.attendance.AttendanceActivity
import com.example.workconnect.ui.auth.AuthViewModel
import com.example.workconnect.ui.tabs.projects.ProjectsAdapter
import com.example.workconnect.utils.Constants.Companion.CHECK_IN
import com.example.workconnect.utils.Constants.Companion.CHECK_OUT
import com.example.workconnect.utils.Constants.Companion.OPERATION_TYPE
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await

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
                        for (task in project.tasks.orEmpty().sortedBy { it?.dateTime }) {
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

        myTasksAdapter.onTakeTaskCheckedListener = MyTasksAdapter.OnCheckClickListener { task, position ->

            handleCheckTask(task.id , task)
        }
        binding.checkInIcon.setOnClickListener {
            val intent = Intent(activity , AttendanceActivity::class.java)
            intent.putExtra(OPERATION_TYPE , CHECK_IN)
            startActivity(intent)
        }

        binding.checkOutIcon.setOnClickListener {
            val intent = Intent(activity , AttendanceActivity::class.java)
            intent.putExtra(OPERATION_TYPE , CHECK_OUT)
            startActivity(intent)
        }

        getProjectsList()
    }

    private fun handleCheckTask(id: String?, task: Task) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Project.PROJECTS_COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val project = document.toObject(Project::class.java)
                    if (project?.tasks != null) {
                        val updatedTasks = project.tasks.map { task ->
                            if (task?.id == id) {
                                task?.copy(taskCompletionState = TaskCompletionState.COMPLETED.state)
                            } else {
                                task
                            }
                        }

                        db.collection(Project.PROJECTS_COLLECTION_NAME)
                            .document(document.id)
                            .update("tasks", updatedTasks)
                            .addOnSuccessListener {
                                Log.d("handleCheckTask", "Task with ID $id updated successfully.")

                                // Update user points and number of completed tasks
                                val userId = viewModel.sessionManager.getUserData()?.id
                                if (userId != null) {
                                    val userRef = db.collection(User.COLLECTION_NAME).document(userId)
                                    userRef.get()
                                        .addOnSuccessListener { userSnapshot ->
                                            val user = userSnapshot.toObject(User::class.java)
                                            val updatedPoints = user?.points!!.toInt() + (task.points?.toInt()
                                                ?: 0)
                                            val updatedNumberOfCompletedTasks =
                                                user?.numberOfCompletedTasks?.toInt()?.plus(1)
                                            userRef.update("points", updatedPoints)
                                            userRef.update("numberOfCompletedTasks", updatedNumberOfCompletedTasks)
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("handleCheckTask", "Error updating user data", exception)
                                        }
                                        .addOnCompleteListener {
                                            // This block ensures that the task is complete before proceeding
                                            getCurrentUserTasks(viewModel.sessionManager.getUserData()?.name.toString())
                                        }
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("handleCheckTask", "Error updating document", exception)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("handleCheckTask", "Error getting documents.", exception)
            }
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