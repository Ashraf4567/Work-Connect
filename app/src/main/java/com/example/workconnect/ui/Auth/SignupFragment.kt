package com.example.workconnect.ui.Auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.workconnect.databinding.FragmentSignupBinding
import com.example.workconnect.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SignupFragment : Fragment() {

    lateinit var binding: FragmentSignupBinding
    private val viewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.addAccountBtn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.addAccount()
                withContext(Dispatchers.Main) {

                }
            }
        }

        initObserevers()
    }

    private fun initObserevers() {
        viewModel.state.observe(viewLifecycleOwner) {
            handleStete(it)
        }
    }

    private fun handleError() {
        Toast.makeText(activity, "something went wrong, try again ", Toast.LENGTH_LONG).show()
        binding.addAccountBtn.isEnabled = true
    }

    private fun handleSuccess() {
        Toast.makeText(
            activity,
            "Account Added Successfully , Please send info to employee ",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun handleStete(state: UiState?) {
        when (state) {
            UiState.ERROR -> handleError()
            UiState.SUCCESS -> handleSuccess()
            else -> {}
        }

    }

}