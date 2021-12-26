package com.piotrokninski.teacherassistant.repository.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import com.piotrokninski.teacherassistant.util.AppConstants

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
        }
    }

    fun getViewType(): String? {
        return PREFS?.getString(AppConstants.VIEW_TYPE, AppConstants.VIEW_TYPE_DEFAULT)
    }

    fun updateViewType(viewType: String) {
        with (PREFS!!.edit()) {
            putString(AppConstants.VIEW_TYPE, viewType)
            apply()
        }
    }
}