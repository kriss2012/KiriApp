package com.apex.asg

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ASGApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code for caching, DI, etc. can go here
    }
}
