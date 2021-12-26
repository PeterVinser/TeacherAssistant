package com.piotrokninski.teacherassistant.util

import android.app.Application
import com.piotrokninski.teacherassistant.repository.room.AppDatabase

class CustomApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        AppDatabase.instantiate(applicationContext)
    }
}