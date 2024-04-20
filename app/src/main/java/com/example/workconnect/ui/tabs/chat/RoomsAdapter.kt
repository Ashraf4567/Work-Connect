package com.route.chatc37.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.workconnect.R
import com.example.workconnect.data.model.Room
import com.example.workconnect.databinding.ItemRoomBinding

class RoomsAdapter(var items: List<Room>? = null) :
    RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding: ItemRoomBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_room, parent, false
            )
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items!![position])
        onItemClickListener?.let { clickListener ->
            holder.viewBinding.root.setOnClickListener {
                clickListener.onItemClick(position, items!![position])

            }

        }
    }

    override fun getItemCount(): Int = items?.size ?: 0
    fun changeData(newList: List<Room>?) {
        items = newList
        notifyDataSetChanged()
    }

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: Room)
    }

    class ViewHolder(val viewBinding: ItemRoomBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(room: Room) {
            viewBinding.item = room
            viewBinding.invalidateAll()
        }
    }
}