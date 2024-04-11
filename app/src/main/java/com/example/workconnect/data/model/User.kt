package com.example.workconnect.data.model

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val type: String? = null,
    val points: Int? = 0,
    val numberOfCompletedTasks: Int = 0
){
    companion object {
        const val MANAGER = "Manger"
        const val EMPLOYEE = "Employee"
        const val COLLECTION_NAME = "users"
    }
}