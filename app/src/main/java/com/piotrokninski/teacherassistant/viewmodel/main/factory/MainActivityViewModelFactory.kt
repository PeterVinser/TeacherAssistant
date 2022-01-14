package com.piotrokninski.teacherassistant.viewmodel.main.factory

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.main.MainActivityViewModel

class MainActivityViewModelFactory(private val sharedPreferences: SharedPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }
}