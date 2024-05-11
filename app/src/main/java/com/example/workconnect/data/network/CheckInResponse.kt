package com.example.workconnect.data.network

import com.google.gson.annotations.SerializedName

data class CheckInResponse(

	@field:SerializedName("checked-in")
	val checkedIn: List<CheckedInItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class CheckedInItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("faces")
	val faces: List<FacesItem?>? = null,

	@field:SerializedName("present")
	val present: Boolean? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null
)

data class FacesItem(

	@field:SerializedName("uuid")
	val uuid: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)
