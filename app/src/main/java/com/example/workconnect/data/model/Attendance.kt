package com.example.workconnect.data.model

data class Attendance(
    val employeeId: String,
    val date: String,
    val checkInTime: String,
    var checkOutTime: String? = null,
    var durationInMinutes: Long = 0
)
