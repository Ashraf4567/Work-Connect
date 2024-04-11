package com.example.workconnect.data.usersRepo

import com.example.workconnect.data.model.User
import com.example.workconnect.utils.UserResult


interface UsersRepository {
    suspend fun insertUserToFirestore(user: User): Result<Unit>
    suspend fun getUserById(userId: String?): UserResult
    suspend fun getAllUsers(): List<User>
}