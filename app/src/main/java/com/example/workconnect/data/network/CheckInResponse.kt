package com.example.workconnect.data.network

data class CheckInResponse(
	val checkedIn: List<CheckedInItem?>? = null,
	val status: String? = null
)
