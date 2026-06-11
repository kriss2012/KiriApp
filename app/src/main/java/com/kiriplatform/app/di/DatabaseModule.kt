package com.kiriplatform.app.di

import android.content.Context
import com.kiriplatform.app.data.local.AalDao
import com.kiriplatform.app.data.local.KiriDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KiriDatabase {
        return KiriDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideAalDao(database: KiriDatabase): AalDao {
        return database.aalDao()
    }
}
