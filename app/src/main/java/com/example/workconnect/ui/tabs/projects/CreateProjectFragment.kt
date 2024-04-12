package com.example.workconnect.ui.tabs.projects

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.workconnect.R
import com.example.workconnect.data.model.Project
import com.example.workconnect.databinding.FragmentCreateProjectBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import kotlin.random.Random


class CreateProjectFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentCreateProjectBinding
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()

        binding.createProjectBtn.setOnClickListener {
            val title = binding.projectName.text.toString().trim()
            val description = binding.projectDecription.text.toString().trim()

            if (title.isEmpty()) {
                binding.projectNameContainer.error = "Please enter a project title"
                return@setOnClickListener // Exit if title is empty
            }

            if (description.isEmpty()) {
                binding.projectDecriptionContainer.error = "Please enter a project description"
                return@setOnClickListener // Exit if description is empty
            }

            val project = Project(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                background = Random.nextInt(1, 3)
            )
            createProjectInFirestore(project)
        }

    }
    private fun createProjectInFirestore(project: Project) {
        firebaseFirestore.collection(Project.PROJECTS_COLLECTION_NAME)
            .add(project)
            .addOnSuccessListener { documentReference ->
                dismiss() // Dismiss dialog on successful creation
                // Handle success (optional: show a toast or confirmation message)
                Toast.makeText(requireContext(), "Project created successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Handle failure (optional: show an error message)
                Log.w("CreateProjectFragment", "Error creating project:", exception)
            }
    }

}

