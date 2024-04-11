package com.example.workconnect.controlPanel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workconnect.data.model.User
import com.example.workconnect.data.usersRepo.UsersRepository
import com.example.workconnect.utils.SingleLiveEvent
import com.example.workconnect.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlPanelViewModel @Inject constructor(
    val usersRepository: UsersRepository,
) : ViewModel() {

    val uIstate = MutableLiveData<UiState>()
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    val uiState = SingleLiveEvent<UiState>()

    val notificationTitle = MutableLiveData<String>()
    val notificationTitleError = MutableLiveData<String?>()
    val notificationMessage = MutableLiveData<String?>()
    val notificationMessageError = MutableLiveData<String?>()

    fun getAllEmployees() {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = usersRepository.getAllUsers()
                _users.postValue(result)
                uIstate.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                Log.e("test get all users", e.message.toString())
                uIstate.postValue(UiState.ERROR)
            }
        }
    }



//    fun sendAlert() {
//        if (!validateSendNotificationForm()) return
//        uiState.postValue(UiState.LOADING)
//        viewModelScope.launch {
//            try {
//                val result = usersRepository.getAllTokens()
//                result.forEach {
//                    PushNotificationService.sendNotificationToDevice(
//                        it.tokenValue!!,
//                        notificationTitle.value!!,
//                        notificationMessage.value!!
//                    )
//                }
//                uiState.postValue(UiState.SUCCESS)
//            } catch (e: Exception) {
//                Log.d("error send Alert", e.message.toString())
//                uiState.postValue(UiState.ERROR)
//            }
//
//        }
//    }

    fun validateSendNotificationForm(): Boolean {
        var isValid = true

        if (notificationTitle.value.isNullOrBlank()) {
            //showError
            notificationTitleError.postValue("برجاء ادخال عنوان التنبيه")
            isValid = false
        } else {
            notificationTitleError.postValue(null)
        }
        if (notificationMessage.value.isNullOrBlank()) {
            //showError
            notificationMessageError.postValue("برجاء ادخال رساله التنبيه")
            isValid = false
        } else {
            notificationMessageError.postValue(null)
        }

        return isValid
    }


}