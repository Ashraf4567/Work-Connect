package com.example.workconnect.ui.tabs.chat.chatRoom

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.workconnect.data.FireStoreUtils
import com.example.workconnect.data.model.Message
import com.example.workconnect.data.model.Room
import com.google.firebase.Timestamp

class ChatRoomViewModel : ViewModel() {
    var room: Room? = null
    val messageField = ObservableField<String>()

    fun sendMessage() {
        if (messageField.get().isNullOrBlank()) return
        val message = Message(
            content = messageField.get(),
            senderName = UserProvider.user?.name,
            senderId = UserProvider.user?.id,
            roomId = room?.id,
            dateTime = Timestamp.now()
        )
        FireStoreUtils().sendMessage(message)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    messageField.set("")
                    return@addOnCompleteListener
                }
                //TODO handle error
            }
    }
}
