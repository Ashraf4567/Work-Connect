package com.example.workconnect.utils

import com.example.workconnect.data.model.User


sealed class UserResult {
    data class Success(val user: User?) : UserResult()
    data class Failure(val exception: Exception) : UserResult()
}