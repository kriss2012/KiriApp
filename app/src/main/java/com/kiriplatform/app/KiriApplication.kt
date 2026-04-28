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
        
        // Firebase initialization is usually handled by the google-services plugin,
        // but ensure it's not manually initialized with missing options elsewhere.
    }
}
