package com.example.workconnect.data.network

import com.google.gson.annotations.SerializedName

data class FaceCompareRequest(
    val providers: String,
    @SerializedName("file1_url") val file1Url: String,
    @SerializedName("file2_url") val file2Url: String,
    @SerializedName("fallback_providers") val fallbackProviders: String
)
