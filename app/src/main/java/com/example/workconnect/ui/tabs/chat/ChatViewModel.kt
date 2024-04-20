package com.example.workconnect.ui.tabs.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.workconnect.data.FireStoreUtils
import com.example.workconnect.data.model.Room

class ChatViewModel : ViewModel() {
    val roomsLiveData = MutableLiveData<List<Room>>()


    fun loadRooms() {
        FireStoreUtils()
            .getAllRooms()
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.e("load rooms", "error loading rooms")
                }
                val rooms = it.result.toObjects(Room::class.java);
                roomsLiveData.value = rooms
            }
    }
}