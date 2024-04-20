package com.example.workconnect.data.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class EnrolPersonResponse(

	@field:SerializedName("collections")
	val collections: List<CollectionsItem?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("faces")
	val faces: List<FacesItem?>? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)