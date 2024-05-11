package com.example.workconnect.data.network

import com.google.gson.annotations.SerializedName

data class CheckoutResponse(

	@field:SerializedName("checked-out")
	val checkedOut: List<CheckedOutItemItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class CheckedOutItemItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null
)
