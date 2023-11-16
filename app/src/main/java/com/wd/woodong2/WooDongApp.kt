package com.wd.woodong2

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //다크모드 비활성화
        FirebaseApp.initializeApp(this)
    }
}