package com.example.workconnect.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.workconnect.R
import com.example.workconnect.databinding.FragmentLoginBinding
import com.example.workconnect.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.loginBtn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logIn()
            }
            binding.loginBtn.isEnabled = false
        }

        observeData()

    }

    private fun observeData() {


        viewModel.state.observe(viewLifecycleOwner) {
            handleState(it)
        }
        viewModel.emailError.observe(viewLifecycleOwner) {
            binding.loginBtn.isEnabled = true
        }
        viewModel.passwordError.observe(viewLifecycleOwner) {
            binding.loginBtn.isEnabled = true
        }
    }

    private fun handleState(state: UiState) {
        when (state) {
            UiState.ERROR -> {
                Toast.makeText(requireActivity(), "something went wrong , try again", Toast.LENGTH_LONG)
                    .show()
                binding.loginBtn.isEnabled = true
            }

            UiState.SUCCESS -> {
                Toast.makeText(requireActivity(), "Logged in successfully ", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)


            }

            else -> {}
        }
    }

}