package com.example.workconnect.ui.tabs.projects

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.workconnect.R
import com.example.workconnect.data.model.Project
import com.example.workconnect.databinding.FragmentProjectsBinding
import com.example.workconnect.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectsFragment : Fragment() {

    lateinit var binding: FragmentProjectsBinding
    private val adapter = ProjectsAdapter(emptyList())
    val bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProjectsBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addProjectBtn.setOnClickListener {
            findNavController().navigate(R.id.action_projectsFragment_to_createProjectFragment)
        }



        initViews()

    }

    private fun getProjectsList() {
        binding.progressCircular.visibility = View.VISIBLE
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

                adapter.submitList(projects)
                binding.progressCircular.visibility = View.GONE
                // Handle retrieved projects (e.g., update UI, perform actions)
            }
            .addOnFailureListener { exception ->
                Log.w("GetProjectsList", "Error getting projects:", exception)
                binding.progressCircular.visibility = View.GONE
                // Handle failure (e.g., show error message)
            }
    }

    private fun initViews() {
        binding.projectsRecycler.adapter = adapter

        getProjectsList()

        adapter.onProjectClickListener = ProjectsAdapter.OnProjectClickListener { id ->

            bundle.putString(Constants.PROJECT_ID , id)
            findNavController().navigate(R.id.action_projectsFragment_to_projectDetailsFragment , bundle)
        }

    }

}