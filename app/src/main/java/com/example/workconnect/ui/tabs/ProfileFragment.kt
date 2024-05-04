package com.example.workconnect.ui.tabs

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
import com.example.workconnect.databinding.FragmentProfileBinding
import com.example.workconnect.ui.auth.AuthViewModel
import com.example.workconnect.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        lifecycleScope.launch {
            viewModel.getUserData(viewModel.sessionManager.getUserData()?.id)

        }
        initObservers()
    }

    private fun initObservers() {
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            binding.user = it
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleUIState(it)
        }
    }

    private fun initViews() {
        binding.lifecycleOwner = this

        binding.logoutBtn.setOnClickListener {
            viewModel.auth.signOut()
            viewModel.sessionManager.logout()
            Toast.makeText(requireActivity(), "تم تسجيل الخروج", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }


    private fun handleUIState(uiState: UiState) {
        when (uiState) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                binding.successView.visibility = View.VISIBLE
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.successView.visibility = View.GONE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireActivity(), "حدث خطأ حاول مره اخري", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}