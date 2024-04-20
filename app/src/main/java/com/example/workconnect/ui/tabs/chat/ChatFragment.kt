package com.example.workconnect.ui.tabs.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workconnect.data.model.Room
import com.example.workconnect.databinding.FragmentChatBinding
import com.example.workconnect.ui.tabs.chat.addRoom.AddRoomActivity
import com.example.workconnect.ui.tabs.chat.chatRoom.ChatRoomActivity
import com.example.workconnect.utils.Constants
import com.route.chatc37.ui.home.RoomsAdapter

class ChatFragment : Fragment() {
    lateinit var binding: FragmentChatBinding
    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        initializeAdapter()
        subscribeToLiveData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addRoomIc.setOnClickListener {
            val intent = Intent(requireContext(), AddRoomActivity::class.java)
            startActivity(intent)
        }
        binding.progressBar.isVisible = true
    }

    private fun subscribeToLiveData() {
        viewModel.roomsLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = false
            adapter.changeData(it)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadRooms()
    }

    val adapter = RoomsAdapter()
    private fun initializeAdapter() {
        binding.roomsRecycler.adapter = adapter
        adapter.onItemClickListener = object : RoomsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, room: Room) {
                val intent = Intent(requireContext(), ChatRoomActivity::class.java);
                intent.putExtra(Constants.EXTRA_ROOM, room)
                startActivity(intent)
            }
        }
    }

}