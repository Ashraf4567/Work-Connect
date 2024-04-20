package com.example.workconnect.ui.tabs.chat.chatRoom

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workconnect.data.FireStoreUtils
import com.example.workconnect.data.model.Message
import com.example.workconnect.data.model.Room
import com.example.workconnect.databinding.ActivityChatRoomBinding
import com.example.workconnect.utils.Constants
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

class ChatRoomActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatRoomBinding
    private val viewModel: ChatRoomViewModel by viewModels()
    var room: Room? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeRoom()
        initializeMessagesAdapter()
        binding.vm = viewModel
        binding.lifecycleOwner = this
        subscribeToMessagesChange()
    }

    val messagesAdapter = MessagesAdapter(mutableListOf())
    lateinit var layoutManager: LinearLayoutManager
    fun initializeMessagesAdapter() {
        layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.messagesRecycler.adapter = messagesAdapter
        binding.messagesRecycler.layoutManager = layoutManager
    }

    var listener: ListenerRegistration? = null
    override fun onStart() {
        super.onStart()
        subscribeToMessagesChange()
    }

    fun subscribeToMessagesChange() {
        if (listener == null) {
            listener = FireStoreUtils()
                .getRoomMessages(room?.id ?: "")
                .addSnapshotListener(
                    EventListener { value, error ->
                        if (error != null) {
                            // we have exception

                            return@EventListener
                        }
                        value?.documentChanges?.forEach {
                            val message = it.document.toObject(Message::class.java)
                            messagesAdapter.addMessage(message)
                            binding.messagesRecycler.smoothScrollToPosition(messagesAdapter.itemCount)
                        }
                    }
                )
        }
    }

    override fun onStop() {
        super.onStop()
        listener?.remove()
        listener = null
    }

    fun initializeRoom() {

        room = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(Constants.EXTRA_ROOM, Room::class.java)
        } else {
            intent.getParcelableExtra(Constants.EXTRA_ROOM)
        }
        viewModel.room = room
        binding.invalidateAll()
    }

}
