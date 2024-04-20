package com.example.workconnect.ui.tabs.chat.addRoom

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.workconnect.databinding.ActivityAddRoomBinding

class AddRoomActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddRoomBinding
    private val viewModel: AddRoomViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.addRoomBtn.setOnClickListener {
            viewModel.createRoom({ finish() })

        }
    }
}