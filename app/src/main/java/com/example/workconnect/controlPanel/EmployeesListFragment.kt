package com.example.workconnect.controlPanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.workconnect.R
import com.example.workconnect.databinding.FragmentEmployeesListBinding
import com.example.workconnect.utils.Constants.Companion.EMPLOYEE_ID
import com.example.workconnect.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeesListFragment : Fragment() {
    lateinit var binding: FragmentEmployeesListBinding
    private val viewModel: ControlPanelViewModel by viewModels()
    private val adapter = EmployeesAdapter(null)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEmployeesListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.users.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.uIstate.observe(viewLifecycleOwner) { it ->
            handleState(it)
        }
    }

    private fun handleState(state: UiState?) {
        when (state) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
            }

            else -> {

            }
        }
    }

    private fun initViews() {
        binding.employeesListRecycler.adapter = adapter
        viewModel.getAllEmployees()

        adapter.onEmployeeClickListener = EmployeesAdapter.OnEmployeeClickListener {employee ->
            val bundle = Bundle()
            bundle.putString(EMPLOYEE_ID, employee?.id)
            findNavController().navigate(R.id.action_employeesListFragment_to_attendanceHistoryFragment , bundle)
        }
    }

}