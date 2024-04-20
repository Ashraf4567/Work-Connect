package com.example.workconnect.ui.tabs.chat.addRoom

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.workconnect.data.FireStoreUtils
import com.example.workconnect.data.model.Room

class AddRoomViewModel : ViewModel() {
    val title = ObservableField<String>()
    val description = ObservableField<String>()
    val titleError = ObservableField<String>()
    val descriptionError = ObservableField<String>()
    var isLoading = MutableLiveData<Boolean>()

    fun createRoom(successCallback: () -> Unit) {
        if (!validateForm()) return

        isLoading.postValue(true)

        val room = Room(
            title = title.get(),
            description = description.get(),
        )

        FireStoreUtils()
            .insertRoom(room)
            .addOnCompleteListener { task ->
                isLoading.postValue(false)

                if (task.isSuccessful) {
                    successCallback.invoke()
                } else {
                    //TODO Handle failure
                }
            }
    }


    fun validateForm(): Boolean {
        var isValid = true
        if (title.get().isNullOrBlank()) {
            isValid = false
            titleError.set("Please enter room title")
        } else {
            titleError.set(null)
        }

        if (description.get().isNullOrBlank()) {
            isValid = false
            descriptionError.set("Please enter room description")
        } else {
            descriptionError.set(null)
        }
        return isValid
    }
}