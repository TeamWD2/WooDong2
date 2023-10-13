package com.wd.woodong2

import android.app.Application
import com.google.firebase.FirebaseApp

class WooDongApp: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}