package com.example.workconnect.data.model

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

data class Message(
    var id: String? = null,
    var content: String? = null,
    var roomId: String? = null,
    var senderId: String? = null,
    var senderName: String? = null,
    var dateTime: Timestamp? = null
) {
    fun formatDateTime(): String {
        val date = dateTime?.toDate() ?: return ""
        val dateFormatter = SimpleDateFormat("hh:mm")

        return dateFormatter.format(date)
    }
}