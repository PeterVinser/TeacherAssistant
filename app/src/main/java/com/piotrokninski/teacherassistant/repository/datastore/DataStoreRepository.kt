package com.piotrokninski.teacherassistant.repository.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.lang.Exception

class DataStoreRepository(private val context: Context) {

    companion object {
        private const val TAG = "DataStoreRepository"

        private val Context.dataStore:
                DataStore<Preferences> by preferencesDataStore(name = Constants.DATA_STORE_SETTINGS)

    }

    suspend fun getString(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.first()[prefKey]
    }

    suspend fun putString(key: String, value: String): Boolean {
        val prefKey = stringPreferencesKey(key)
        return try {
            context.dataStore.edit { preferences ->
                preferences[prefKey] = value
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "putString: ", e)

            false
        }
    }

    object Constants {
        const val DATA_STORE_SETTINGS = "settings"

        const val VIEW_TYPE = "viewType"
    }
}