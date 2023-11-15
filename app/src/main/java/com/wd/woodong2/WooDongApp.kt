package com.wd.woodong2

import android.app.Application
import com.google.firebase.FirebaseApp

class WooDongApp: Application() {
    companion object {
        @Volatile
        private lateinit var app: WooDongApp

        @JvmStatic
        fun getApp(): WooDongApp {
            return app
        }
    }
    override fun onCreate() {
        app = this
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}