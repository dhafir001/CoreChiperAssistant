package com.corechiper.assistant

import android.app.Application
import com.corechiper.assistant.core.notification.NotificationHelper

class CoreChiperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
