package com.example.workconnect.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.workconnect.data.local.SessionManagerImpl
import com.example.workconnect.data.network.WebServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {


    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor {
                message -> Log.d("api Log", message ?:"") }

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return loggingInterceptor
    }


    @Provides
    fun provideTokenInterceptor(): TokenInterceptor {
        return TokenInterceptor()
    }

    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        tokenInterceptor: TokenInterceptor
    ):OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()
    }

    @Provides
    fun provideGsonConvertorFactory():GsonConverterFactory{
        return GsonConverterFactory.create()
    }


        @Provides
        fun provideRetrofit(
            client: OkHttpClient,
        converter:GsonConverterFactory
        ):Retrofit{
            return Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.luxand.cloud/")
                .addConverterFactory(converter)
                .build()
        }
    @Provides
    fun provideWebServices(retrofit: Retrofit): WebServices {
        return retrofit.create(WebServices::class.java)
    }
}