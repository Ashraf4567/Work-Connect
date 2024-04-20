package com.example.workconnect.controlPanel

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.workconnect.R
import com.example.workconnect.databinding.FragmentManagerProfileBinding
import com.example.workconnect.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagerProfileFragment : Fragment() {

    lateinit var binding: FragmentManagerProfileBinding
    private val viewModel: AuthViewModel by viewModels()


    private val sectionsList =
        listOf(
            "My Account",
            "Add tasks requests",
            "Employees List",
            "Add Account"
        )
    private val sectionsAdapter = ManagerSectionsAdapter(sectionsList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManagerProfileBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sectionsRecycler.adapter = sectionsAdapter

        initViews()
    }

    private fun initViews() {
        binding.sectionsRecycler.adapter = sectionsAdapter

        sectionsAdapter.onSectionClickListener =
            ManagerSectionsAdapter.OnSectionClickListener {
                handleSectionsNavigation(it)
            }
        binding.logoutBtn.setOnClickListener {
            viewModel.auth.signOut()
            viewModel.sessionManager.logout()
            Toast.makeText(requireActivity(), "logout successfully", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_managerProfileFragment_to_loginFragment)
        }
        binding.icSendAlert.setOnClickListener {
            //findNavController().navigate(R.id.action_managerProfileFragment_to_sendNotificationFragment)
        }
    }

    private fun handleSectionsNavigation(it: String) {
        when (it) {
            "Add Account"->{
                val intent = Intent(activity , AddAccountActivity::class.java)
                startActivity(intent)
            }
            "Employees List"->{
                findNavController().navigate(R.id.action_managerProfileFragment_to_employeesListFragment)
            }
        }
    }

}