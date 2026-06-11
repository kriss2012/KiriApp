package com.kiriplatform.app.di

import com.kiriplatform.app.data.local.AalDao
import com.kiriplatform.app.data.remote.AalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAalRepository(aalDao: AalDao): AalRepository {
        return AalRepository(aalDao = aalDao)
    }
}
