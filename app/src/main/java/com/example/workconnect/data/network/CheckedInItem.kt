package com.example.workconnect.data.network

data class CheckedInItem(
	val name: String? = null,
	val faces: List<FacesItem?>? = null,
	val present: Boolean? = null,
	val uuid: String? = null
)
