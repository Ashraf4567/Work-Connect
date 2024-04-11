package com.example.workconnect.di

import com.example.workconnect.data.local.SessionManager
import com.example.workconnect.data.local.SessionManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class BindModule {
    @Binds
    abstract fun bindSessionManager(manager: SessionManagerImpl): SessionManager

}