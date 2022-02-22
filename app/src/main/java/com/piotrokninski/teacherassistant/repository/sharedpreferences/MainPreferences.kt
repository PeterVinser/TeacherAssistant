package com.piotrokninski.teacherassistant.repository.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import java.lang.IllegalStateException

object MainPreferences {

    @Volatile
    private var PREFS: SharedPreferences? = null

    private const val PREFS_NAME = "params"

    fun instantiate(context: Context) {
        synchronized(this) {
            var prefs = PREFS
            if (prefs == null) {
                prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            }
            PREFS = prefs
            updateViewType(AppConstants.VIEW_TYPE_STUDENT)
        }
    }

    fun getInstance(): SharedPreferences {
        return PREFS ?: throw IllegalStateException("Main preferences has not been instantiated")
    }

    fun getViewType(): String? {
        return PREFS?.getString(AppConstants.VIEW_TYPE, AppConstants.VIEW_TYPE_BLANK)
    }

    fun updateViewType(viewType: String) {
        with (PREFS!!.edit()) {
            putString(AppConstants.VIEW_TYPE, viewType)
            apply()
        }
    }
}