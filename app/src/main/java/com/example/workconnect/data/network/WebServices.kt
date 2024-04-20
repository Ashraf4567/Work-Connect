package com.example.workconnect.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface WebServices {
    @Multipart
    @POST("v2/person")
    suspend fun enrolPerson(
        @Part("name") name: RequestBody,
        @Part("store") store: RequestBody,
        @Part("collections") collections: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<EnrolPersonResponse>

    @Multipart
    @POST("attendance/check/in")
    suspend fun checkIn(
        @Part("room") room: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<CheckInResponse>

    @Multipart
    @POST("attendance/check/in")
    suspend fun checkOut(
        @Part("room") room: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<CheckInResponse>

}