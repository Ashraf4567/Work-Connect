package com.example.workconnect.di

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "69d3593e4b404181bb1fbcf42826be02"
        val originalRequest = chain.request()
        val requestWithToken = originalRequest.newBuilder()
            .addHeader("token", token) // Add your token header here
            .build()

        return chain.proceed(requestWithToken)
    }
}