package com.example.workconnect.ui.attendance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.workconnect.R
import com.example.workconnect.databinding.FragmentAttendanceHistoryBinding
import com.example.workconnect.utils.Constants
import com.example.workconnect.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttendanceHistoryFragment : Fragment() {

    private val viewModel: AttendanceViewModel by viewModels()
    private lateinit var binding: FragmentAttendanceHistoryBinding
    private val adapter = AttendanceHistoryAdapter(null)

    var employeeId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAttendanceHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        employeeId = arguments?.getString(Constants.EMPLOYEE_ID) ?: ""
        initViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.uiState.observe(viewLifecycleOwner){
            handleUiState(it)
        }
        viewModel.attendanceHistory.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

    private fun handleUiState(state: UiState) {

        when(state){
            UiState.SUCCESS -> {
                binding.attendanceRecycler.visibility = View.VISIBLE
                binding.loadingAnim.visibility = View.GONE
            }
            UiState.LOADING -> {
                binding.attendanceRecycler.visibility = View.GONE
                binding.loadingAnim.visibility = View.VISIBLE
            }
            UiState.ERROR -> {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews() {
        viewModel.getAttendanceHistory(employeeId)
        binding.attendanceRecycler.adapter = adapter

    }

}