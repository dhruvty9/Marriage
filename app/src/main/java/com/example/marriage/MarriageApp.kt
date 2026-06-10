package com.example.marriage

import android.app.Application
import com.google.firebase.FirebaseApp

class MarriageApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase is usually initialized automatically by the Google Services plugin
        // but explicit initialization ensures it's ready for any background tasks.
        FirebaseApp.initializeApp(this)
    }
}
