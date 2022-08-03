package com.piotrokninski.teacherassistant.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class DataStoreRepository(private val context: Context) {

    companion object {

        private val Context.dataStore:
                DataStore<Preferences> by preferencesDataStore(name = Constants.DATA_STORE_SETTINGS)

//        @Volatile
//        private var PREFS: DataStore<Preferences>? = null
//
//        private const val PREFS_NAME = "params"
//
//        fun instantiate(context: Context) {
//            synchronized(this) {
//                var prefs = PREFS
//                if (prefs == null) {
//                    prefs = context.preferencesDataStoreFile(name = ) .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//                }
//                PREFS = prefs
//                MainPreferences.updateViewType(AppConstants.VIEW_TYPE_STUDENT)
//            }
//        }
    }

    suspend fun getString(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.first()[prefKey]
    }

    suspend fun putString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    object Constants {
        const val DATA_STORE_SETTINGS = "settings"

        const val VIEW_TYPE = "viewType"
    }
}