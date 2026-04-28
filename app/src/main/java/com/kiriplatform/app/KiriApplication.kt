package com.kiriplatform.app

import android.app.Application
import com.kiriplatform.app.utils.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KiriApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize global components
        NotificationHelper.createNotificationChannel(this)
    }
}
