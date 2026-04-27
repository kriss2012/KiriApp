package com.kiriplatform.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KiriApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code for caching, DI, etc. can go here
    }
}
