package com.example.workconnect

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.workconnect.databinding.ActivityMainBinding
import com.example.workconnect.ui.auth.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        viewModel.checkUserType()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setupWithNavController(navController)

        // Add an OnDestinationChangedListener to the NavController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Check if the current destination is the signup or login fragment
            val isSignupOrLogin =
                destination.id == R.id.loginFragment || destination.id == R.id.projectDetailsFragment
                        ||destination.id == R.id.addTaskFragment
            updateBottomNavBarVisibility(!isSignupOrLogin)

        }
        var isManager = false
        viewModel.isManager.observe(this){
            isManager = it
        }

        bottomNavigationView.setOnItemSelectedListener { item ->

            handleBottomNavigation(item.itemId, isManager)

            true

        }


    }

    private fun handleBottomNavigation(itemId: Int, isManager: Boolean) {
        when (itemId) {
            R.id.profileFragment -> {

                val destinationId =
                    if (isManager) R.id.managerProfileFragment else R.id.profileFragment
                findNavController(R.id.container).navigate(destinationId)

            }

            else -> {
                findNavController(R.id.container).navigate(itemId)
            }
        }
    }

    private fun updateBottomNavBarVisibility(visible: Boolean) {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        if (visible) {
            bottomNavView.visibility = View.VISIBLE
            binding.bottomNavigationView.visibility = View.VISIBLE
        } else {
            bottomNavView.visibility = View.GONE
            binding.bottomNavigationView.visibility = View.GONE
            val container = binding.container

            // Get the layout params of the container
            val layoutParams = container.layoutParams as ViewGroup.MarginLayoutParams

            // Set bottom margin to 0dp
            layoutParams.bottomMargin = 0

            // Update the container's layout params
            container.layoutParams = layoutParams
        }
    }
}