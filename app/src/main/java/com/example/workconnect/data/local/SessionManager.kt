package com.example.workconnect.data.local

import com.example.workconnect.data.model.User


interface SessionManager {
    fun saveUserData(user: User?)
    fun getUserData(): User?
    fun logout()
}