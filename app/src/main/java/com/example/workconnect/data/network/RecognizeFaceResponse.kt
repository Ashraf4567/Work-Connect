package com.example.workconnect.data.network

import com.google.gson.annotations.SerializedName

data class RecognizeFaceResponse(

	@field:SerializedName("RecognizeFaceResponse")
	val recognizeFaceResponse: List<RecognizeFaceResponseItem?>? = null
)

data class RecognizeFaceResponseItem(

	@field:SerializedName("collections")
	val collections: List<CollectionsItem?>? = null,

	@field:SerializedName("probability")
	val probability: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("rectangle")
	val rectangle: Rectangle? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null
)

data class Rectangle(

	@field:SerializedName("top")
	val top: Int? = null,

	@field:SerializedName("left")
	val left: Int? = null,

	@field:SerializedName("bottom")
	val bottom: Int? = null,

	@field:SerializedName("right")
	val right: Int? = null
)

