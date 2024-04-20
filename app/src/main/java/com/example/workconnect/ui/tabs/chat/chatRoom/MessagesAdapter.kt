package com.example.workconnect.ui.tabs.chat.chatRoom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workconnect.data.model.Message
import com.example.workconnect.databinding.ItemRecievedMessageBinding
import com.example.workconnect.databinding.ItemSentMessageBinding

class MessagesAdapter(var messages: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class SentMessageViewHolder(val itemBinding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(message: Message) {
            itemBinding.setMessage(message)
            itemBinding.executePendingBindings()
        }
    }

    class ReceivedMessageViewHolder(val itemBinding: ItemRecievedMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(message: Message) {
            itemBinding.setMessage(message)
            itemBinding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        if (message.senderId == UserProvider.user?.id) {

            return MessageType.Sent.value
        } else {
            return MessageType.Received.value
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == MessageType.Sent.value) {
            val itemBinding =
                ItemSentMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SentMessageViewHolder(itemBinding)
        } else {
            val itemBinding = ItemRecievedMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ReceivedMessageViewHolder(itemBinding)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is SentMessageViewHolder -> {
                holder.bind(messages[position])
            }

            is ReceivedMessageViewHolder -> {
                holder.bind(messages[position])
            }
        }
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size)
    }


    enum class MessageType(val value: Int) {
        Received(200),
        Sent(100)
    }
}